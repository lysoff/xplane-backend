package app.xplne.backend.util

import app.xplne.backend.model.Activity
import app.xplne.backend.model.Model
import app.xplne.backend.model.Resource
import java.util.*

class TestDataGenerator() {
    companion object {
        fun createModels() = listOf(
            Model(id = UUID.randomUUID(), name = "Basic model"),
            Model(id = UUID.randomUUID(), name = "Advanced model"),
            Model(id = UUID.randomUUID(), name = "Superhero model")
        )

        fun createResources() = listOf(
            Resource(id = UUID.randomUUID(), name = "Vigor"),
            Resource(id = UUID.randomUUID(), name = "Well-being"),
            Resource(id = UUID.randomUUID(), name = "Motivation")
        )

        fun createActivities() = listOf(
            Activity(id = UUID.randomUUID(), name = "Sleeping"),
            Activity(id = UUID.randomUUID(), name = "Workout"),
            Activity(id = UUID.randomUUID(), name = "Eating junk food")
        )
    }
}
