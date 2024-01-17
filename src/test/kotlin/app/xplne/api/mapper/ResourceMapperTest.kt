package app.xplne.api.mapper

import app.xplne.api.dto.ActivityDto
import app.xplne.api.dto.ResourceDto
import app.xplne.api.model.Activity
import app.xplne.api.model.Resource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*


class ResourceMapperTest {

    private val mapper = Mappers.getMapper(ResourceMapper::class.java)

    @Test
    fun givenResourceDto_whenMapToEntity_thenSuccessfulMapping() {
        // GIVEN
        val dto = ResourceDto(UUID.randomUUID(), "Resource name")
        // WHEN
        val entity = mapper.toEntity(dto)
        // THEN
        assertEquals(dto.id, entity.id)
        assertEquals(dto.name, entity.name)
    }

    @Test
    fun givenResource_whenMapToDto_thenSuccessfulMapping() {
        // GIVEN
        val entity = Resource(UUID.randomUUID(), "Resource name")
        // WHEN
        val dto = mapper.toDto(entity)
        // THEN
        assertNotNull(dto)
        assertEquals(entity.id, dto!!.id)
        assertEquals(entity.name, dto.name)
    }
}