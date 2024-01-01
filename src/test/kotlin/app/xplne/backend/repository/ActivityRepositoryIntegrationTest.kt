package app.xplne.backend.repository

import app.xplne.backend.annotation.RepositoryIntegrationTest
import app.xplne.backend.model.Activity
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
class ActivityRepositoryIntegrationTest(
    @Autowired private val activityRepository: ActivityRepository,
    @Autowired private val template: R2dbcEntityTemplate
) {
    @Test
    fun givenNewActivity_whenSaveIsCalled_thenItIsInsertedInDB() {
        // GIVEN
        val activity = Activity(name = "New activity")
        // WHEN
        val insertedMono: Mono<Activity> = activityRepository.save(activity)
        // THEN
        insertedMono
            .mapNotNull { insertedActivity ->
                assertNotNull(insertedActivity.id)
                insertedActivity.id!!
            }
            .flatMap(this::findById)
            .testWithTx()
            .assertNext { foundActivity ->
                assertNotNull(foundActivity.id)
                assertEquals(activity.name, foundActivity.name)
            }
            .verifyComplete()
    }

    @Test
    fun givenChangedActivity_whenSaveIsCalled_thenItIsUpdatedInDB() {
        // GIVEN
        val initialActivity = Activity(id = UUID.randomUUID(), name = "Initial name")
        val insertedMono = insertActivities(listOf(initialActivity))
        // WHEN
        val changedActivity = initialActivity.copy(name = "Changed name")
        val updatedMono = insertedMono
            .then(activityRepository.save(changedActivity))
        // THEN
        updatedMono
            .then(findById(initialActivity.id))
            .testWithTx()
            .expectNext(changedActivity)
            .verifyComplete()
    }

    @Test
    fun givenExistingActivitiesInDB_whenFindAllIsCalled_thenAllAreReturned() {
        // GIVEN
        val expectedActivities: List<Activity> = createActivityExamples()
        val insertedMono: Mono<Activity> = insertActivities(expectedActivities)
        // WHEN
        val foundFlux: Flux<Activity> = insertedMono
            .thenMany(activityRepository.findAll())
        // THEN
        foundFlux.collectList()
            .testWithTx()
            .assertNext { foundActivities ->
                assertEquals(expectedActivities.size, foundActivities.size)
                assertTrue(foundActivities.containsAll(expectedActivities))
            }
            .verifyComplete()
    }

    @Test
    fun givenExistingActivitiesInDB_whenFindByIdIsCalled_thenOneIsReturned() {
        // GIVEN
        val existingActivities: List<Activity> = createActivityExamples()
        val insertedMono: Mono<Activity> = insertActivities(existingActivities)
        // WHEN
        val expectedActivity = existingActivities[0]
        val foundMono: Mono<Activity> = insertedMono
            .then(activityRepository.findById(expectedActivity.id!!))
        // THEN
        foundMono
            .testWithTx()
            .expectNext(expectedActivity)
            .verifyComplete()
    }

    @Test
    fun givenExistingActivitiesInDB_whenDeleteByIdIsCalled_thenItIsExecuted() {
        // GIVEN
        val existingActivities: List<Activity> = createActivityExamples()
        val insertedMono: Mono<Activity> = insertActivities(existingActivities)
        // WHEN
        val expectedActivities = existingActivities.toMutableList()
        val activityToDelete = expectedActivities.removeAt(0)
        val deletedMono = insertedMono
            .then(activityRepository.deleteById(activityToDelete.id!!))
        // THEN
        deletedMono
            .thenMany(findAll())
            .collectList()
            .testWithTx()
            .assertNext { foundActivities ->
                assertFalse(foundActivities.contains(activityToDelete))
                assertEquals(expectedActivities.size, foundActivities.size)
                assertTrue(foundActivities.containsAll(expectedActivities))
            }
            .verifyComplete()
    }

    private fun createActivityExamples() = listOf(
        Activity(id = UUID.randomUUID(), name = "Sleeping"),
        Activity(id = UUID.randomUUID(), name = "Workout"),
        Activity(id = UUID.randomUUID(), name = "Eating junk food")
    )

    private fun insertActivities(expectedActivities: List<Activity>): Mono<Activity> {
        var insertedMono: Mono<Activity> = Mono.empty()
        expectedActivities.forEach {
            insertedMono = insertedMono.then(template.insert(it))
        }
        return insertedMono
    }

    private fun findAll(): Flux<Activity> {
        return template.select(Activity::class.java).all()
    }

    private fun findById(uuid: UUID?): Mono<Activity> {
        return template.select(Activity::class.java)
            .matching(query(where("id").`is`(uuid!!)))
            .one()
    }

}
