package app.xplne.backend.repository

import app.xplne.backend.annotation.RepositoryIntegrationTest
import app.xplne.backend.model.ModelResource
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
class ModelResourceRepositoryIntegrationTest(
    @Autowired private val repository: ModelResourceRepository,
    @Autowired private val template: R2dbcEntityTemplate
) {
    private val generated = TestDataGenerator()

    @Test
    fun givenNewModelResource_whenUpsertIsCalled_thenItIsInsertedInDB() {
        // GIVEN
        val model = generated.basicModel
        val resource = generated.basicResources[0]
        val dbPrepared = template.insert(model)
            .then(template.insert(resource))
        // WHEN
        val modelResource = ModelResource(model.id!!, resource.id!!, 100)
        val insertedMono = dbPrepared.then(repository.upsert(modelResource))
        // THEN
        insertedMono
            .thenMany(findAllByModelId(model.id!!))
            .collectList()
            .testWithTx()
            .assertNext { foundList ->
                assertEquals(1, foundList.size)
                assertTrue(foundList.contains(modelResource))
            }
            .verifyComplete()
    }

    @Test
    fun givenChangedModelResource_whenUpsertIsCalled_thenItIsUpdatedInDB() {
        // GIVEN
        val model = generated.basicModel
        val resource = generated.basicResources[0]
        val modelResource = ModelResource(model.id!!, resource.id!!, amount = 100)
        val dbPrepared = template.insert(model)
            .then(template.insert(resource))
            .then(template.insert(modelResource))
        // WHEN
        val changedModelResource = modelResource.copy(amount = 50)
        val insertedMono = dbPrepared.then(
            repository.upsert(changedModelResource)
        )
        // THEN
        insertedMono
            .thenMany(findAllByModelId(model.id!!))
            .collectList()
            .testWithTx()
            .assertNext { foundList ->
                assertEquals(1, foundList.size)
                assertTrue(foundList.contains(changedModelResource))
            }
            .verifyComplete()
    }

    @Test
    fun givenExistingModelResourcesInDB_whenFindByModelIsCalled_thenAllAreReturned() {
        // GIVEN
        val expectedEntities = generated.basicModelResources
        val dbPrepared = fillDatabase()
        // WHEN
        val foundFlux: Flux<ModelResource> = dbPrepared
            .thenMany(repository.findAllByModelId(generated.basicModel.id!!))
        // THEN
        foundFlux.collectList()
            .testWithTx()
            .assertNext { foundList ->
                assertEquals(expectedEntities.size, foundList.size)
                assertTrue(foundList.containsAll(expectedEntities))
            }
            .verifyComplete()
    }

    @Test
    fun givenExistingEntityInDB_whenDeleteByModelAndResourceIsCalled_thenItIsExecuted() {
        // GIVEN
        val dbPrepared = fillDatabase()
        // WHEN
        val expectedEntities = generated.basicModelResources.toMutableList()
        val deletable = expectedEntities.removeAt(0)
        val deletedMono = dbPrepared.then(repository
            .deleteByModelIdAndResourceId(deletable.modelId, deletable.resourceId)
        )
        // THEN
        deletedMono
            .thenMany(findAllByModelId(generated.basicModel.id!!))
            .collectList()
            .testWithTx()
            .assertNext { foundList ->
                assertFalse(foundList.contains(deletable))
                assertEquals(expectedEntities.size, foundList.size)
                assertTrue(foundList.containsAll(expectedEntities))
            }
            .verifyComplete()
    }

    private fun fillDatabase(): Mono<Void> =
        // Insert data for tests
        template.insert(generated.basicModel)
            .then(template.insertAll(generated.basicResources))
            .then(template.insertAll(generated.basicModelResources))
            // Add some surrounding data to create a more realistic environment
            .then(template.insert(generated.superheroModel))
            .then(template.insertAll(generated.superheroResources))
            .then(template.insertAll(generated.superheroModelResources))
            .then()

    private fun findAllByModelId(modelId: UUID): Flux<ModelResource> {
        return template.select(ModelResource::class.java)
            .matching(query(where("model_id").`is`(modelId)))
            .all()
    }
}
