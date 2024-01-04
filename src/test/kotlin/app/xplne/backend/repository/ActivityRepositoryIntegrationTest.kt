package app.xplne.backend.repository

import app.xplne.backend.annotation.RepositoryIntegrationTest
import app.xplne.backend.model.Activity
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
class ActivityRepositoryIntegrationTest(
    @Autowired private val activityRepository: ActivityRepository,
    @Autowired private val template: R2dbcEntityTemplate
) {
    private val generated = TestDataGenerator()

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
        val insertedMono = template.insert(initialActivity)
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
        val expectedActivities: List<Activity> = generated.basicActivities
        val insertedMono: Mono<Activity> = template.insertAll(expectedActivities)
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
        val existingActivities: List<Activity> = generated.basicActivities
        val insertedMono: Mono<Activity> = template.insertAll(existingActivities)
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
        val existingActivities: List<Activity> = generated.basicActivities
        val insertedMono: Mono<Activity> = template.insertAll(existingActivities)
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

    private fun findAll(): Flux<Activity> {
        return template.select(Activity::class.java).all()
    }

    private fun findById(uuid: UUID?): Mono<Activity> {
        return template.select(Activity::class.java)
            .matching(query(where("id").`is`(uuid!!)))
            .one()
    }

}
