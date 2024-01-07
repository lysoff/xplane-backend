package app.xplne.api.repository

import app.xplne.api.annotation.RepositoryIntegrationTest
import app.xplne.api.model.Model
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.EnableTransactionManagement

@RepositoryIntegrationTest
@EnableTransactionManagement
class ModelRepositoryIntegrationTest(
    @Autowired val modelRepository: ModelRepository,
    @Autowired val entityManager: TestEntityManager
) {
    @Test
    fun givenNewModel_whenSave_thenInsertInDB() {
        // GIVEN
        val model = Model(name = "New model")
        // WHEN
        val saved = modelRepository.save(model)
        // THEN
        assertNotNull(saved.id)
        assertEquals(model.name, saved.name)
        val found = entityManager.find(Model::class.java, saved.id)
        assertEquals(model, found)
    }

    @Test
    fun givenChangedModel_whenSave_thenUpdateInDB() {
        // GIVEN
        val initial = Model(name = "Initial name")
        val saved = entityManager.persist(initial)
        // WHEN
        val changed = saved.copy(name = "Changed name")
        val updated = modelRepository.save(changed)
        // THEN
        assertEquals(changed, updated)
        val found = entityManager.find(Model::class.java, saved.id)
        assertEquals(changed, found)
    }

    @Test
    fun givenModelsInDB_whenFindAll_thenReturnAll() {
        // GIVEN
        val expectedModels = createModels()
        expectedModels.forEach(entityManager::persist)
        // WHEN
        val foundModels: MutableList<Model> = modelRepository.findAll()
        // THEN
        assertEquals(expectedModels.size, foundModels.size)
        assertTrue(expectedModels.containsAll(foundModels))
    }

    @Test
    fun givenModelInDB_whenFindByID_thenReturnIt() {
        // GIVEN
        val models = createModels()
        models.forEach(entityManager::persist)
        val expected = models[0]
        // WHEN
        val found: Model? = modelRepository.findByIdOrNull(expected.id!!)
        // THEN
        assertNotNull(found)
        assertEquals(expected, found)
    }

    @Test
    fun givenModelInDB_whenDeleteById_thenItIsDeleted() {
        // GIVEN
        val models = createModels()
        models.forEach(entityManager::persist)
        val expectedModels = models.toMutableList()
        val modelToDelete = expectedModels.removeAt(0)
        // WHEN
        modelRepository.deleteById(modelToDelete.id!!)
        // THEN
        val found = entityManager.find(Model::class.java, modelToDelete.id)
        assertNull(found)
    }

    private fun createModels() = listOf(
        Model(name = "Basic model"),
        Model(name = "Advanced model"),
        Model(name = "Superhero model")
    )
}