package app.xplne.backend.repository

import app.xplne.backend.annotation.RepositoryIntegrationTest
import app.xplne.backend.model.Model
import app.xplne.backend.util.TestDataGenerator
import app.xplne.backend.util.insertAll
import com.chikli.spring.rxtx.testWithTx
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RepositoryIntegrationTest
class ModelRepositoryIntegrationTest(
    @Autowired private val modelRepository: ModelRepository,
    @Autowired private val template: R2dbcEntityTemplate
) {
    private val generated = TestDataGenerator()
    
    @Test
    fun givenNewModel_whenSaveIsCalled_thenItIsInsertedInDB() {
        // GIVEN
        val model = Model(name = "New model")
        // WHEN
        val insertedMono: Mono<Model> = modelRepository.save(model)
        // THEN
        insertedMono
            .mapNotNull { insertedModel ->
                assertNotNull(insertedModel.id)
                insertedModel.id!!
            }
            .flatMap(this::findById)
            .testWithTx()
            .assertNext { foundModel ->
                assertNotNull(foundModel.id)
                assertEquals(model.name, foundModel.name)
            }
            .verifyComplete()
    }

    @Test
    fun givenChangedModel_whenSaveIsCalled_thenItIsUpdatedInDB() {
        // GIVEN
        val initialModel = Model(id = UUID.randomUUID(), name = "Initial name")
        val insertedMono = template.insert(initialModel)
        // WHEN
        val changedModel = initialModel.copy(name = "Changed name")
        val updatedMono = insertedMono
            .then(modelRepository.save(changedModel))
        // THEN
        updatedMono
            .then(findById(initialModel.id))
            .testWithTx()
            .expectNext(changedModel)
            .verifyComplete()
    }

    @Test
    fun givenExistingModelsInDB_whenFindAllIsCalled_thenAllAreReturned() {
        // GIVEN
        val expectedModels: List<Model> = generated.allModels
        val insertedMono: Mono<Model> = template.insertAll(expectedModels)
        // WHEN
        val foundFlux: Flux<Model> = insertedMono
            .thenMany(modelRepository.findAll())
        // THEN
        foundFlux.collectList()
            .testWithTx()
            .assertNext { foundModels ->
                assertEquals(expectedModels.size, foundModels.size)
                assertTrue(foundModels.containsAll(expectedModels))
            }
            .verifyComplete()
    }

    @Test
    fun givenExistingModelsInDB_whenFindByIdIsCalled_thenOneIsReturned() {
        // GIVEN
        val existingModels: List<Model> = generated.allModels
        val insertedMono: Mono<Model> = template.insertAll(existingModels)
        // WHEN
        val expectedModel = existingModels[0]
        val foundMono: Mono<Model> = insertedMono
            .then(modelRepository.findById(expectedModel.id!!))
        // THEN
        foundMono
            .testWithTx()
            .expectNext(expectedModel)
            .verifyComplete()
    }

    @Test
    fun givenExistingModelsInDB_whenDeleteByIdIsCalled_thenItIsExecuted() {
        // GIVEN
        val existingModels: List<Model> = generated.allModels
        val insertedMono: Mono<Model> = template.insertAll(existingModels)
        // WHEN
        val expectedModels = existingModels.toMutableList()
        val modelToDelete = expectedModels.removeAt(0)
        val deletedMono = insertedMono
            .then(modelRepository.deleteById(modelToDelete.id!!))
        // THEN
        deletedMono
            .thenMany(findAll())
            .collectList()
            .testWithTx()
            .assertNext { foundModels ->
                assertFalse(foundModels.contains(modelToDelete))
                assertEquals(expectedModels.size, foundModels.size)
                assertTrue(foundModels.containsAll(expectedModels))
            }
            .verifyComplete()
    }

    private fun findAll(): Flux<Model> {
        return template.select(Model::class.java).all()
    }

    private fun findById(uuid: UUID?): Mono<Model> {
        return template.select(Model::class.java)
            .matching(query(where("id").`is`(uuid!!)))
            .one()
    }

}
