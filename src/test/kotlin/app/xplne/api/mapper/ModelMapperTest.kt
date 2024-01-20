package app.xplne.api.mapper

import app.xplne.api.dto.ModelActivityDto
import app.xplne.api.dto.ModelResourceDto
import app.xplne.api.model.ModelActivity
import app.xplne.api.model.ModelResource
import app.xplne.api.util.TestData
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers


class ModelMapperTest {

    private val mapper = Mappers.getMapper(ModelMapper::class.java)

    @Test
    fun givenModel_whenMapToFullDto_thenSuccessfulMapping() {
        // GIVEN
        val model = TestData.getBasicModel()
        // WHEN
        val dto = mapper.toFullDto(model)
        // THEN
        assertNotNull(dto)
        assertEquals(model.id, dto.id)
        assertEquals(model.name, dto.name)
        verifyModelActivities(dto.activities, model.activities)
        verifyModelResources(dto.resources, model.resources)
    }

    private fun verifyModelActivities(
        verifiableDtos: List<ModelActivityDto>?,
        sourceEntities: List<ModelActivity>
    ) {
        assertNotNull(verifiableDtos)
        assertEquals(sourceEntities.size, verifiableDtos!!.size)
        sourceEntities.forEachIndexed { index, expected ->
            val dto: ModelActivityDto = verifiableDtos[index]
            assertEquals(expected.activity.id, dto.activityId)
            assertEquals(expected.activity.name, dto.name)
            assertNull(dto.impacts)
        }
    }

    private fun verifyModelResources(
        verifiableDtos: List<ModelResourceDto>?,
        sourceEntities: MutableList<ModelResource>
    ) {
        assertNotNull(verifiableDtos)
        assertEquals(sourceEntities.size, verifiableDtos!!.size)
        sourceEntities.forEachIndexed { index, modelResource ->
            val dto: ModelResourceDto = verifiableDtos[index]
            assertEquals(modelResource.resource.id, dto.resourceId)
            assertEquals(modelResource.resource.name, dto.name)
            assertEquals(modelResource.amount, dto.amount)
        }
    }
}