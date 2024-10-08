package app.xplne.api.repository

import app.xplne.api.annotation.JpaIntegrationTest
import app.xplne.api.model.*
import app.xplne.api.util.TestData
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import java.util.*


@JpaIntegrationTest
class ModelRepositoryIntegrationTest(
    @Autowired val modelRepository: ModelRepository,
    @Autowired val entityManager: TestEntityManager
) {
    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenNewModel_whenSave_thenInsertInDB() {
        // GIVEN
        val model = Model(name = "New model")
        copyActivitiesAndResources(model, TestData.basicModel)
        // WHEN
        val saved = modelRepository.save(model)
        // THEN
        assertNotNull(saved.id)
        assertEquals(model.name, saved.name)
        verifyModelInDb(model, saved.id!!)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenModelInDb_whenSaveItWithChangedName_thenUpdateInDB() {
        // GIVEN
        val modelInDb = TestData.basicModel
        // WHEN
        val changed = modelInDb.copy(name = "Changed name")
        val updated = modelRepository.save(changed)
        // THEN
        assertEquals(changed.name, updated.name)
        verifyModelInDb(changed, modelInDb.id!!)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenModelInDb_whenSaveItWithDeletedResource_thenUpdateInDB() {
        // GIVEN
        val basicModel = TestData.basicModel
        // WHEN
        val changedResources = basicModel.resources.apply { removeAt(0) }
        val changedModel = basicModel.copy(resources = changedResources)
        val updated = modelRepository.save(changedModel)
        // THEN
        assertEquals(changedResources.size, updated.resources.size)
        assertTrue(changedResources.containsAll(updated.resources))
        verifyModelInDb(changedModel, basicModel.id!!)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenModelInDb_whenDeleteById_thenItIsDeleted() {
        // GIVEN
        val modelToDelete = TestData.basicModel
        // WHEN
        modelRepository.deleteById(modelToDelete.id!!)
        // THEN
        val found = entityManager.find(Model::class.java, modelToDelete.id)
        assertNull(found)
        // assert dictionary records were not deleted, only connections with them
        assertResourcesNotDeleted(modelToDelete)
        assertActivitiesNotDeleted(modelToDelete)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    @Sql("classpath:sql/insert-superhero-model.sql")
    fun givenDbHasModels_whenFindAll_thenReturnAll() {
        // GIVEN
        val expectedModels = listOf(TestData.basicModel, TestData.superheroModel)
        // WHEN
        val foundModels: MutableList<Model> = modelRepository.findAll()
        // THEN
        assertEquals(expectedModels.size, foundModels.size)
        assertTrue(expectedModels.containsAll(foundModels))
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    @Sql("classpath:sql/insert-superhero-model.sql")
    fun givenDbHasModels_whenFindById_thenReturnIt() {
        // GIVEN
        val expected = TestData.basicModel
        // WHEN
        val found: Model? = modelRepository.findByIdOrNull(expected.id!!)
        // THEN
        assertNotNull(found)
        validateModel(expected, found!!)
    }

    private fun verifyModelInDb(expectedModel: Model, modelId: UUID) {
        // save and clear persistence context, otherwise entity manager could return the same object as expected
        entityManager.flush()
        entityManager.clear()
        val actual = entityManager.find(Model::class.java, modelId)
        validateModel(expectedModel, actual)
    }

    private fun validateModel(expectedModel: Model, actualModel: Model) {
        // assert we don't compare the object with itself
        assertFalse(expectedModel === actualModel)
        assertEquals(expectedModel, actualModel)
        assertEquals(expectedModel.id, actualModel.id)
        assertEquals(expectedModel.name, actualModel.name)

        validateModelResources(expectedModel.resources, actualModel.resources)
        validateModelActivities(expectedModel.activities, actualModel.activities)
    }

    private fun validateModelResources(
        expectedResources: List<ModelResource>,
        actualResources: List<ModelResource>
    ) {
        assertEquals(expectedResources.size, actualResources.size)
        expectedResources.forEach { expectedResource ->
            val foundResource = actualResources.find { expectedResource.id!! == it.id }
            assertNotNull(foundResource)
            assertEquals(expectedResource, foundResource)
            assertEquals(expectedResource.amount, foundResource!!.amount)
        }
    }

    private fun validateModelActivities(
        expectedActivities: List<ModelActivity>,
        actualActivities: List<ModelActivity>
    ) {
        assertEquals(expectedActivities.size, actualActivities.size)
        expectedActivities.forEach { expectedResource ->
            val foundResource = actualActivities.find { expectedResource.id!! == it.id }
            assertNotNull(foundResource)
            assertEquals(expectedResource, foundResource)
        }
    }

    private fun copyActivitiesAndResources(targetModel: Model, sourceModel: Model) {
        val basicModel = entityManager.find(Model::class.java, sourceModel.id)
        basicModel.resources.forEach { modelResource ->
            targetModel.addResource(modelResource.resource, modelResource.amount)
        }
        basicModel.activities.forEach { basicModelActivity ->
            targetModel.addActivity(basicModelActivity.activity)
        }
    }

    private fun assertResourcesNotDeleted(deletedModel: Model) {
        deletedModel.resources.forEach {
            val foundModelResource = entityManager.find(ModelResource::class.java, it.id)
            assertNull(foundModelResource)
            val foundResource = entityManager.find(Resource::class.java, it.resource.id)
            assertNotNull(foundResource)
        }
    }

    private fun assertActivitiesNotDeleted(deletedModel: Model) {
        deletedModel.activities.forEach {
            val modelActivity = entityManager.find(ModelActivity::class.java, it.id)
            assertNull(modelActivity)
            val activity = entityManager.find(Activity::class.java, it.activity.id)
            assertNotNull(activity)
        }
    }
}