package app.xplne.api.repository

import app.xplne.api.annotation.JpaIntegrationTest
import app.xplne.api.model.Activity
import app.xplne.api.repository.common.findByIdOrNull
import app.xplne.api.util.ACTIVITY_EAT_JUNK_FOOD_ID
import app.xplne.api.util.TestData
import app.xplne.api.util.TestData.Companion.getBasicActivity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.test.context.jdbc.Sql
import java.util.*


@JpaIntegrationTest
class ActivityRepositoryIntegrationTest(
    @Autowired val activityRepository: ActivityRepository,
    @Autowired val entityManager: TestEntityManager
) {
    @Test
    fun givenNewActivity_whenPersist_thenInsertInDB() {
        // GIVEN
        val activity = Activity(name = "New activity")
        // WHEN
        val saved = activityRepository.persist(activity)
        // THEN
        assertNotNull(saved.id)
        assertEquals(activity.name, saved.name)
        verifyActivityInDb(activity, saved.id!!)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenActivityInDb_whenUpdateChangedName_thenUpdateInDB() {
        // GIVEN
        val activityInDb: Activity = TestData.getBasicActivities()[0]
        // WHEN
        val changed = activityInDb.copy(name = "Changed name")
        val updated = activityRepository.update(changed)
        // THEN
        assertEquals(changed.name, updated.name)
        verifyActivityInDb(changed, activityInDb.id!!)
    }

    @Test
    fun givenNonExistingId_whenUpdate_thenThrow() {
        // GIVEN
        val nonExisting = Activity(UUID.randomUUID(), "Non-existing activity")
        // WHEN-THEN
        assertThrows<ObjectOptimisticLockingFailureException> {
            activityRepository.update(nonExisting)
            activityRepository.flush()
        }
    }

    @Test
    fun givenActivityInDb_whenDeleteById_thenItIsDeleted() {
        // GIVEN
        val activity = Activity(name = "Activity to delete")
        entityManager.persistAndFlush(activity)
        // WHEN
        activityRepository.deleteById(activity.id!!)
        // THEN
        entityManager.flush()
        entityManager.clear()
        val found = entityManager.find(Activity::class.java, activity.id)
        assertNull(found)
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenDbHasActivities_whenFindAll_thenReturnAll() {
        // GIVEN
        val pageable = PageRequest.of(0, 10, Sort.by("name").ascending())
        val expectedActivities = TestData.getBasicActivities()
        // WHEN
        val foundActivities: Slice<Activity> = activityRepository.findAll(pageable)
        // THEN
        assertTrue(foundActivities.isLast)
        assertEquals(expectedActivities.size, foundActivities.content.size)
        expectedActivities.forEach { expected ->
            val found = foundActivities.find { it == expected }
            validateActivity(expected, found)
        }
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    fun givenDbHasMoreActivitiesThanPageSize_whenFindAll_thenReturnFirstPage() {
        // GIVEN
        // inserted activities: Workout, Eating junk food
        val pageable = PageRequest.of(0, 1, Sort.by("name").ascending())
        // with page size = 1 and sorting by name first page should contain only "Eating junk food"
        val expectedActivity = getBasicActivity(ACTIVITY_EAT_JUNK_FOOD_ID)
        // WHEN
        val foundActivities: Slice<Activity> = activityRepository.findAll(pageable)
        // THEN
        assertTrue(foundActivities.hasNext())
        assertEquals(1, foundActivities.content.size)
        validateActivity(expectedActivity, foundActivities.first())
    }

    @Test
    @Sql("classpath:sql/insert-basic-model.sql")
    @Sql("classpath:sql/insert-superhero-model.sql")
    fun givenDbHasActivities_whenFindById_thenReturnIt() {
        // GIVEN
        val expected: Activity = TestData.getBasicActivities()[0]
        // WHEN
        val found = activityRepository.findByIdOrNull(expected.id!!)
        // THEN
        assertNotNull(found)
        validateActivity(expected, found!!)
    }

    private fun verifyActivityInDb(expected: Activity, id: UUID) {
        // save and clear persistence context, otherwise entity manager could return the same object as expected
        entityManager.flush()
        entityManager.clear()
        val actual = entityManager.find(Activity::class.java, id)
        validateActivity(expected, actual)
    }

    private fun validateActivity(expectedActivity: Activity, actualActivity: Activity?) {
        // assert we don't compare the object with itself
        assertFalse(expectedActivity === actualActivity)
        assertEquals(expectedActivity, actualActivity)
        assertEquals(expectedActivity.id, actualActivity!!.id)
        assertEquals(expectedActivity.name, actualActivity.name)
    }
}
