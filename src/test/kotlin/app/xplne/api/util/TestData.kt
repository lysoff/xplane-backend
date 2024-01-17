package app.xplne.api.util

import app.xplne.api.model.Activity
import app.xplne.api.model.Model
import app.xplne.api.model.Resource
import org.junit.jupiter.api.Assertions
import java.util.*

private const val MODEL_BASIC_ID            = "2d4e6d50-5883-48e2-bac1-d33c660fa75a"
private const val RESOURCE_VIGOR_ID         = "e040b0c0-dfbc-49e1-b315-ce2d5c449c15"
private const val RESOURCE_WELL_BEING_ID    = "a7a3d9bb-180c-427b-9f5d-e139e8da1f53"
private const val ACTIVITY_WORKOUT_ID       = "abd6f39b-b386-48ce-8598-06c20482c38b"
const val ACTIVITY_EAT_JUNK_FOOD_ID         = "48651546-92c2-4e33-b583-38d1f237bd51"

private const val MODEL_SUPERHERO_ID        = "a4fe64d1-9779-465a-9754-78c683eab335"
private const val RESOURCE_SUPERPOWER_ID    = "2b4311ac-028a-439c-94a4-a5cd2f0b5fdb"
private const val ACTIVITY_FLYING_ID        = "2d487791-61c2-4ba5-9c13-3c1374835912"

class TestData {
    companion object {
        fun getBasicActivity(uuidAsString: String): Activity {
            val activity = getBasicActivities().find {
                it.id == UUID.fromString(uuidAsString) }
            Assertions.assertNotNull(activity)
            return activity!!
        }

        fun getBasicModel(): Model {
            val basicModel = Model(name = "Basic Model", id = UUID.fromString(MODEL_BASIC_ID))

            val idToResourceMap: Map<String, Resource> = mapResourcesByTheirIds(getBasicResources())
            basicModel.addResource(idToResourceMap[RESOURCE_VIGOR_ID]!!, 100)
            basicModel.addResource(idToResourceMap[RESOURCE_WELL_BEING_ID]!!, 50)

            getBasicActivities().forEach(basicModel::addActivity)
            return basicModel
        }

        fun getBasicResources(): List<Resource> = listOf(
            Resource(name = "Vigor", id = UUID.fromString(RESOURCE_VIGOR_ID)),
            Resource(name = "Well-being", id = UUID.fromString(RESOURCE_WELL_BEING_ID))
        )

        fun getBasicActivities(): List<Activity> = listOf(
            Activity(name = "Workout", id = UUID.fromString(ACTIVITY_WORKOUT_ID)),
            Activity(name = "Eating junk food", id = UUID.fromString(ACTIVITY_EAT_JUNK_FOOD_ID))
        )

        fun getSuperheroModel(): Model {
            val model = Model(name = "Superhero Model", id = UUID.fromString(MODEL_SUPERHERO_ID))
            model.addResource(getSuperpowerResource(), 99)
            model.addActivity(getSuperheroActivity())
            return model
        }

        fun getSuperpowerResource() =
            Resource(name = "Superpower", id = UUID.fromString(RESOURCE_SUPERPOWER_ID))

        private fun getSuperheroActivity() =
            Activity(name = "Flying", id = UUID.fromString(ACTIVITY_FLYING_ID))

        private fun mapResourcesByTheirIds(resources: List<Resource>) =
            resources.associateBy { it.id.toString() }
    }
}
