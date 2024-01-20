package app.xplne.api.mapper

import app.xplne.api.dto.ActivityDto
import app.xplne.api.model.Activity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*


class ActivityMapperTest {

    private val mapper = Mappers.getMapper(ActivityMapper::class.java)

    @Test
    fun givenActivityDto_whenMapToEntity_thenSuccessfulMapping() {
        // GIVEN
        val dto = ActivityDto(UUID.randomUUID(), "Activity name")
        // WHEN
        val entity = mapper.toEntity(dto)
        // THEN
        assertEquals(dto.id, entity.id)
        assertEquals(dto.name, entity.name)
    }

    @Test
    fun givenActivity_whenMapToDto_thenSuccessfulMapping() {
        // GIVEN
        val entity = Activity(UUID.randomUUID(), "Activity name")
        // WHEN
        val dto = mapper.toDto(entity)
        // THEN
        assertNotNull(dto)
        assertEquals(entity.id, dto!!.id)
        assertEquals(entity.name, dto.name)
    }
}