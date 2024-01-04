package app.xplne.backend.util

import app.xplne.backend.model.Activity
import app.xplne.backend.model.Model
import app.xplne.backend.model.ModelResource
import app.xplne.backend.model.Resource
import java.util.UUID.randomUUID
import kotlin.random.Random

class TestDataGenerator() {
    companion object {
        fun createModels() = listOf(
            createBasicModel(),
            Model(id = randomUUID(), name = "Advanced model"),
            Model(id = randomUUID(), name = "Superhero model")
        )

        fun createBasicModel() = Model(id = randomUUID(), name = "Basic model")

        fun createResourcesForBasicModel() = listOf(
            Resource(id = randomUUID(), name = "Vigor"),
            Resource(id = randomUUID(), name = "Well-being"),
            Resource(id = randomUUID(), name = "Motivation")
        )

        fun createModelResources(model: Model, resources: List<Resource>) =
            resources.map { ModelResource(model.id!!, it.id!!, Random.nextInt(100).toShort()) }

        fun createActivities() = listOf(
            Activity(id = randomUUID(), name = "Sleeping"),
            Activity(id = randomUUID(), name = "Workout"),
            Activity(id = randomUUID(), name = "Eating junk food")
        )
    }
}
