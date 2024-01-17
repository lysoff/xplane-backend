package app.xplne.api.repository

import app.xplne.api.annotation.JpaIntegrationTest
import app.xplne.api.model.Resource
import app.xplne.api.repository.common.findByIdOrNull
import app.xplne.api.util.TestData
import jakarta.persistence.OptimisticLockException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.test.context.jdbc.Sql
import java.util.*


@JpaIntegrationTest
class ResourceRepositoryIntegrationTest(
    @Autowired val resourceRepository: ResourceRepository,
    @Autowired val entityManager: TestEntityManager
) {
    @Test
    fun givenNewResource_whenPersist_thenInsertInDB() {
        // GIVEN
        val resource = Resource(name = "New resource")
        // WHEN
        val saved = resourceRepository.persist(resource)
        // THEN
        assertNotNull(saved.id)
        assertEquals(resource.name, saved.name)
        verifyResourceInDb(resource, saved.id!!)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenResourceInDb_whenUpdateChangedName_thenUpdateInDB() {
        // GIVEN
        val resourceInDb: Resource = TestData.getBasicResources()[0]
        // WHEN
        val changed = resourceInDb.copy(name = "Changed name")
        val updated = resourceRepository.update(changed)
        // THEN
        assertEquals(changed.name, updated.name)
        verifyResourceInDb(changed, resourceInDb.id!!)
    }

    @Test
    fun givenNonExistingId_whenUpdate_thenThrow() {
        // GIVEN
        val nonExisting = Resource(UUID.randomUUID(), "Non-existing resource")
        // WHEN-THEN
        org.junit.jupiter.api.assertThrows<OptimisticLockException> {
            resourceRepository.update(nonExisting)
            entityManager.flush()
        }
    }

    @Test
    fun givenResourceInDb_whenDeleteById_thenItIsDeleted() {
        // GIVEN
        val resource = Resource(name = "Resource to delete")
        entityManager.persistAndFlush(resource)
        // WHEN
        resourceRepository.deleteById(resource.id!!)
        // THEN
        entityManager.flush()
        entityManager.clear()
        val found = entityManager.find(Resource::class.java, resource.id)
        assertNull(found)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenDbHasResources_whenFindAll_thenReturnAll() {
        // GIVEN
        val pageable = PageRequest.of(0, 10, Sort.by("name").ascending())
        val expectedResources = TestData.getBasicResources()
        // WHEN
        val foundResources: Slice<Resource> = resourceRepository.findAll(pageable)
        // THEN
        assertTrue(foundResources.isLast)
        assertEquals(expectedResources.size, foundResources.content.size)
        expectedResources.forEach { expected ->
            val found = foundResources.find { it == expected }
            validateResource(expected, found)
        }
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    @Sql("classpath:sql/insert-superhero-model.sql")
    fun givenDbHasMoreResourcesThanPageSize_whenFindAll_thenReturnFirstPage() {
        // GIVEN
        // inserted resources: Vigor, Well-being, Superpower
        val pageable = PageRequest.of(0, 1, Sort.by("name").ascending())
        // with page size = 1 and sorting by name first page should contain only "Superpower"
        val expectedResource = TestData.getSuperpowerResource()
        // WHEN
        val foundResources: Slice<Resource> = resourceRepository.findAll(pageable)
        // THEN
        assertTrue(foundResources.hasNext())
        assertEquals(1, foundResources.content.size)
        validateResource(expectedResource, foundResources.first())
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    @Sql("classpath:sql/insert-superhero-model.sql")
    fun givenDbHasResources_whenFindById_thenReturnIt() {
        // GIVEN
        val expected: Resource = TestData.getBasicResources()[0]
        // WHEN
        val found = resourceRepository.findByIdOrNull(expected.id!!)
        // THEN
        assertNotNull(found)
        validateResource(expected, found!!)
    }

    private fun verifyResourceInDb(expected: Resource, id: UUID) {
        // save and clear persistence context, otherwise entity manager could return the same object as expected
        entityManager.flush()
        entityManager.clear()
        val actual = entityManager.find(Resource::class.java, id)
        validateResource(expected, actual)
    }

    private fun validateResource(expectedResource: Resource, actualResource: Resource?) {
        // assert we don't compare the object with itself
        assertFalse(expectedResource === actualResource)
        assertEquals(expectedResource, actualResource)
        assertEquals(expectedResource.id, actualResource!!.id)
        assertEquals(expectedResource.name, actualResource.name)
    }
}
