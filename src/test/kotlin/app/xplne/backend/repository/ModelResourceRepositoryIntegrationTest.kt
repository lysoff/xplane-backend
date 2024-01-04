package app.xplne.backend.repository

import app.xplne.backend.annotation.RepositoryIntegrationTest
import app.xplne.backend.model.Model
import app.xplne.backend.model.ModelResource
import app.xplne.backend.model.Resource
import app.xplne.backend.util.TestDataGenerator.Companion.createBasicModel
import app.xplne.backend.util.TestDataGenerator.Companion.createModelResources
import app.xplne.backend.util.TestDataGenerator.Companion.createResourcesForBasicModel
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
import java.util.UUID.randomUUID

@RepositoryIntegrationTest
class ModelResourceRepositoryIntegrationTest(
    @Autowired private val modelResourceRepository: ModelResourceRepository,
    @Autowired private val template: R2dbcEntityTemplate
) {
    @Test
    fun givenNewModelResource_whenUpsertIsCalled_thenItIsInsertedInDB() {
        // GIVEN
        val model = Model(id = randomUUID(), name = "Basic model")
        val resource = Resource(id = randomUUID(), name = "Vigor")
        val givenMono = template.insert(model).then(template.insert(resource))

        val modelResource = ModelResource(model.id!!, resource.id!!, 100)
        // WHEN
        val insertedMono = givenMono.then(modelResourceRepository.upsert(modelResource))
        // THEN
        insertedMono
            .thenMany(findAllByModelId(model.id!!))
            .collectList()
            .testWithTx()
            .assertNext { modelResources ->
                assertEquals(1, modelResources.size)
                assertTrue(modelResources.contains(modelResource))
            }
            .verifyComplete()
    }

    @Test
    fun givenChangedModelResource_whenUpsertIsCalled_thenItIsUpdatedInDB() {
        // GIVEN
        val model = Model(id = randomUUID(), name = "Basic model")
        val resource = Resource(id = randomUUID(), name = "Vigor")
        val modelResource = ModelResource(model.id!!, resource.id!!, 100)
        val givenMono = template.insert(model)
            .then(template.insert(resource))
            .then(template.insert(modelResource))
        // WHEN
        val changedModelResource = modelResource.copy(amount = 50)
        val insertedMono = givenMono.then(
            modelResourceRepository.upsert(changedModelResource))
        // THEN
        insertedMono
            .thenMany(findAllByModelId(model.id!!))
            .collectList()
            .testWithTx()
            .assertNext { modelResources ->
                assertEquals(1, modelResources.size)
                assertTrue(modelResources.contains(changedModelResource))
            }
            .verifyComplete()
    }

    @Test
    fun givenExistingModelResourcesInDB_whenFindByModelIsCalled_thenAllAreReturned() {
        // GIVEN
        val model = createBasicModel()
        val resources = createResourcesForBasicModel()
        val modelResources = createModelResources(model, resources)

        val insertedMono: Mono<ModelResource> = template
            .insert(model)
            .then(template.insertAll(resources))
            .then(template.insertAll(modelResources))
        // WHEN
        val foundFlux: Flux<ModelResource> = insertedMono
            .thenMany(modelResourceRepository.findAllByModelId(model.id!!))
        // THEN
        foundFlux.collectList()
            .testWithTx()
            .assertNext { foundModelResources ->
                assertEquals(modelResources.size, foundModelResources.size)
                assertTrue(foundModelResources.containsAll(modelResources))
            }
            .verifyComplete()
    }

    @Test
    fun givenExistingEntityInDB_whenDeleteByModelAndResourceIsCalled_thenItIsExecuted() {
        // GIVEN
        val model = createBasicModel()
        val resources = createResourcesForBasicModel()
        val modelResources = createModelResources(model, resources)

        val insertedMono: Mono<ModelResource> = template
            .insert(model)
            .then(template.insertAll(resources))
            .then(template.insertAll(modelResources))
        // WHEN
        val expectedModelResources = modelResources.toMutableList()
        val deletable = expectedModelResources.removeAt(0)
        val deletedMono = insertedMono
            .then(modelResourceRepository.deleteByModelIdAndResourceId(
                deletable.modelId, deletable.resourceId))
        // THEN
        deletedMono
            .thenMany(findAllByModelId(model.id!!))
            .collectList()
            .testWithTx()
            .assertNext { foundModelResources ->
                assertFalse(foundModelResources.contains(deletable))
                assertEquals(expectedModelResources.size, foundModelResources.size)
                assertTrue(foundModelResources.containsAll(expectedModelResources))
            }
            .verifyComplete()
    }

    private fun findAllByModelId(modelId: UUID): Flux<ModelResource> {
        return template.select(ModelResource::class.java)
            .matching(query(where("model_id").`is`(modelId)))
            .all()
    }
}
