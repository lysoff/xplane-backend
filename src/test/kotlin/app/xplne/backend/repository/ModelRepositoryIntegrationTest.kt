package app.xplne.backend.repository

import app.xplne.backend.config.TestDatabaseConfiguration
import app.xplne.backend.model.Model
import com.chikli.spring.rxtx.RxTestTransaction
import com.chikli.spring.rxtx.testWithTx
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@DataR2dbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(TestDatabaseConfiguration::class, RxTestTransaction::class)
class ModelRepositoryIntegrationTest(
    @Autowired private val modelRepository: ModelRepository,
    @Autowired private val template: R2dbcEntityTemplate
) {
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
        val insertedMono = insertModels(listOf(initialModel))
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
        val expectedModels: List<Model> = createModelExamples()
        val insertedMono: Mono<Model> = insertModels(expectedModels)
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
        val existingModels: List<Model> = createModelExamples()
        val insertedMono: Mono<Model> = insertModels(existingModels)
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
        val existingModels: List<Model> = createModelExamples()
        val insertedMono: Mono<Model> = insertModels(existingModels)
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

    private fun createModelExamples() = listOf(
        Model(id = UUID.randomUUID(), name = "Basic model"),
        Model(id = UUID.randomUUID(), name = "Advanced model"),
        Model(id = UUID.randomUUID(), name = "Superhero model")
    )

    private fun insertModels(expectedModels: List<Model>): Mono<Model> {
        var savedModelsMono: Mono<Model> = Mono.empty()
        expectedModels.forEach {
            savedModelsMono = savedModelsMono.then(template.insert(it))
        }
        return savedModelsMono
    }

    private fun findAll() = template.select(Model::class.java).all()

    private fun findById(uuid: UUID?): Mono<Model> {
        return template.select(Model::class.java)
            .matching(query(where("id").`is`(uuid!!)))
            .one()
    }

}
