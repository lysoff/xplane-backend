package app.xplne.api.mapper

import app.xplne.api.model.Activity
import app.xplne.api.model.Model
import app.xplne.api.model.ModelActivity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*


class ModelActivityMapperTest {

    private val mapper = Mappers.getMapper(ModelActivityMapper::class.java)

    @Test
    fun givenModelActivity_whenMapToDto_thenSuccessfulMapping() {
        // GIVEN
        val model = Model(UUID.randomUUID(), "Model name")
        val activity = Activity(UUID.randomUUID(), "Activity name")
        val modelActivity = ModelActivity(model, activity)
        // WHEN
        val dto = mapper.toDto(modelActivity)
        // THEN
        assertNotNull(dto)
        assertEquals(activity.id, dto.activityId)
        assertEquals(activity.name, dto.name)
    }
}