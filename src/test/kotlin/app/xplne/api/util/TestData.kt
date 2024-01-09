package app.xplne.api.util

import app.xplne.api.model.Model
import app.xplne.api.model.Resource
import java.util.*

private const val MODEL_BASIC_ID = "2d4e6d50-5883-48e2-bac1-d33c660fa75a"
private const val RESOURCE_VIGOR_ID = "e040b0c0-dfbc-49e1-b315-ce2d5c449c15"
private const val RESOURCE_WELL_BEING_ID = "a7a3d9bb-180c-427b-9f5d-e139e8da1f53"
private const val MODEL_SUPERHERO_ID = "a4fe64d1-9779-465a-9754-78c683eab335"
private const val RESOURCE_SUPERPOWER_ID = "2b4311ac-028a-439c-94a4-a5cd2f0b5fdb"

class TestData {
    companion object {
        val basicResources: List<Resource> = createBasicResources()
        val basicModel: Model = createBasicModel()

        val superpowerResource = Resource(name = "Superpower", id = UUID.fromString(RESOURCE_SUPERPOWER_ID))
        val superheroModel: Model = createSuperheroModel()

        private fun createBasicModel(): Model {
            val basicModel = Model(name = "Basic Model", id = UUID.fromString(MODEL_BASIC_ID))
            val idToResourceMap: Map<String, Resource> = mapResourcesByTheirIds(basicResources)
            basicModel.addResource(idToResourceMap[RESOURCE_VIGOR_ID]!!, 100)
            basicModel.addResource(idToResourceMap[RESOURCE_WELL_BEING_ID]!!, 50)
            return basicModel
        }

        private fun createBasicResources(): List<Resource> = listOf(
            Resource(name = "Vigor", id = UUID.fromString(RESOURCE_VIGOR_ID)),
            Resource(name = "Well-being", id = UUID.fromString(RESOURCE_WELL_BEING_ID))
        )

        private fun createSuperheroModel(): Model {
            val model = Model(name = "Superhero Model", id = UUID.fromString(MODEL_SUPERHERO_ID))
            model.addResource(superpowerResource, 99)
            return model
        }

        private fun mapResourcesByTheirIds(resources: List<Resource>) =
            resources.associateBy { it.id.toString() }
    }
}
