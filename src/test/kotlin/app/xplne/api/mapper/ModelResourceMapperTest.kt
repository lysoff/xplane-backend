package app.xplne.api.mapper

import app.xplne.api.model.Model
import app.xplne.api.model.ModelResource
import app.xplne.api.model.Resource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*


class ModelResourceMapperTest {

    private val mapper = Mappers.getMapper(ModelResourceMapper::class.java)

    @Test
    fun givenModelResource_whenMapToDto_thenSuccessfulMapping() {
        // GIVEN
        val model = Model(UUID.randomUUID(), "Model name")
        val resource = Resource(UUID.randomUUID(), "Resource name")
        val modelResource = ModelResource(model, resource, amount = 100)
        // WHEN
        val dto = mapper.toDto(modelResource)
        // THEN
        assertNotNull(dto)
        assertEquals(resource.id, dto.resourceId)
        assertEquals(resource.name, dto.name)
        assertEquals(modelResource.amount, dto.amount)
    }
}