package app.xplne.backend.util

import app.xplne.backend.model.Activity
import app.xplne.backend.model.Model
import app.xplne.backend.model.ModelResource
import app.xplne.backend.model.Resource
import java.util.UUID.randomUUID
import kotlin.random.Random

class TestDataGenerator {

    val allModels by lazy { listOf(basicModel, superheroModel) }

    val basicModel by lazy { Model(randomUUID(), "Basic model") }
    val basicResources by lazy {
        listOf(
            Resource(randomUUID(), "Vigor"),
            Resource(randomUUID(), "Well-being"),
            Resource(randomUUID(), "Motivation")
        )
    }
    val basicModelResources by lazy { createModelResources(basicModel, basicResources) }
    val basicActivities by lazy {
        listOf(
            Activity(randomUUID(), "Sleeping"),
            Activity(randomUUID(), "Workout"),
            Activity(randomUUID(), "Eating junk food")
        )
    }

    val superheroModel by lazy { Model(randomUUID(), "Superhero model") }
    val superheroResources by lazy {
        listOf(
            Resource(randomUUID(), "Superpower")
        )
    }
    val superheroModelResources by lazy { createModelResources(superheroModel, superheroResources) }

    companion object {
        fun createModelResources(model: Model, resources: List<Resource>) =
            resources.map {
                ModelResource(model.id!!, it.id!!, Random.nextInt(100).toShort())
            }
    }
}
