package app.xplne.backend.repository

import app.xplne.backend.annotation.RepositoryIntegrationTest
import app.xplne.backend.model.Resource
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
class ResourceRepositoryIntegrationTest(
    @Autowired private val resourceRepository: ResourceRepository,
    @Autowired private val template: R2dbcEntityTemplate
) {
    private val generated = TestDataGenerator()

    @Test
    fun givenNewResource_whenSaveIsCalled_thenItIsInsertedInDB() {
        // GIVEN
        val resource = Resource(name = "New resource")
        // WHEN
        val insertedMono: Mono<Resource> = resourceRepository.save(resource)
        // THEN
        insertedMono
            .mapNotNull { insertedResource ->
                assertNotNull(insertedResource.id)
                insertedResource.id!!
            }
            .flatMap(this::findById)
            .testWithTx()
            .assertNext { foundResource ->
                assertNotNull(foundResource.id)
                assertEquals(resource.name, foundResource.name)
            }
            .verifyComplete()
    }

    @Test
    fun givenChangedResource_whenSaveIsCalled_thenItIsUpdatedInDB() {
        // GIVEN
        val initialResource = Resource(id = UUID.randomUUID(), name = "Initial name")
        val insertedMono = template.insert(initialResource)
        // WHEN
        val changedResource = initialResource.copy(name = "Changed name")
        val updatedMono = insertedMono
            .then(resourceRepository.save(changedResource))
        // THEN
        updatedMono
            .then(findById(initialResource.id))
            .testWithTx()
            .expectNext(changedResource)
            .verifyComplete()
    }

    @Test
    fun givenExistingResourcesInDB_whenFindAllIsCalled_thenAllAreReturned() {
        // GIVEN
        val expectedResources: List<Resource> = generated.basicResources
        val insertedMono: Mono<Resource> = template.insertAll(expectedResources)
        // WHEN
        val foundFlux: Flux<Resource> = insertedMono
            .thenMany(resourceRepository.findAll())
        // THEN
        foundFlux.collectList()
            .testWithTx()
            .assertNext { foundResources ->
                assertEquals(expectedResources.size, foundResources.size)
                assertTrue(foundResources.containsAll(expectedResources))
            }
            .verifyComplete()
    }

    @Test
    fun givenExistingResourcesInDB_whenFindByIdIsCalled_thenOneIsReturned() {
        // GIVEN
        val existingResources: List<Resource> = generated.basicResources
        val insertedMono: Mono<Resource> = template.insertAll(existingResources)
        // WHEN
        val expectedResource = existingResources[0]
        val foundMono: Mono<Resource> = insertedMono
            .then(resourceRepository.findById(expectedResource.id!!))
        // THEN
        foundMono
            .testWithTx()
            .expectNext(expectedResource)
            .verifyComplete()
    }

    @Test
    fun givenExistingResourcesInDB_whenDeleteByIdIsCalled_thenItIsExecuted() {
        // GIVEN
        val existingResources: List<Resource> = generated.basicResources
        val insertedMono: Mono<Resource> = template.insertAll(existingResources)
        // WHEN
        val expectedResources = existingResources.toMutableList()
        val resourceToDelete = expectedResources.removeAt(0)
        val deletedMono = insertedMono
            .then(resourceRepository.deleteById(resourceToDelete.id!!))
        // THEN
        deletedMono
            .thenMany(findAll())
            .collectList()
            .testWithTx()
            .assertNext { foundResources ->
                assertFalse(foundResources.contains(resourceToDelete))
                assertEquals(expectedResources.size, foundResources.size)
                assertTrue(foundResources.containsAll(expectedResources))
            }
            .verifyComplete()
    }

    private fun findAll(): Flux<Resource> {
        return template.select(Resource::class.java).all()
    }

    private fun findById(uuid: UUID?): Mono<Resource> {
        return template.select(Resource::class.java)
            .matching(query(where("id").`is`(uuid!!)))
            .one()
    }
}
