package app.xplne.api.controller

import app.xplne.api.constants.BASE_PATH_MODELS
import app.xplne.api.constants.PATH_MODEL_ID
import app.xplne.api.dto.ModelActivityDto
import app.xplne.api.dto.ModelFullDto
import app.xplne.api.dto.ModelResourceDto
import app.xplne.api.dto.ModelShortView
import app.xplne.api.service.ModelService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.projection.SpelAwareProxyProjectionFactory
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import java.util.UUID.randomUUID


@WebMvcTest(ModelController::class)
@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class ModelControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var modelService: ModelService

    private val projectionFactory = SpelAwareProxyProjectionFactory()

    @DisplayName("GET all models returns a list of short DTOs")
    @Test
    fun givenTwoModels_whenGetAll_thenReturnAllWith200() {
        // GIVEN
        val firstModel = createShortView("First model")
        val secondModel = createShortView("Second model")
        val modelShortViews = listOf(firstModel, secondModel)
        every { modelService.findAll() } returns modelShortViews
        // WHEN-THEN
        mockMvc.get(BASE_PATH_MODELS)
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    modelShortViews.forEachIndexed { index, view ->
                        jsonPath("$[$index].id") { value(view.id.toString()) }
                        jsonPath("$[$index].name") { value(view.name) }
                    }
                }
            }
        verify(exactly = 1) { modelService.findAll() }
    }

    @DisplayName("GET existing model by ID returns a detailed DTO with 200")
    @Test
    fun givenExistingId_whenGetById_thenReturnItWith200() {
        // GIVEN
        val dto = createModelFullDto()
        every { modelService.findByIdOrNull(dto.id!!) } returns dto
        // WHEN-THEN
        val url = BASE_PATH_MODELS + PATH_MODEL_ID
        mockMvc.get(url, dto.id).andExpect {
            status { isOk() }
            assertContainsModelFullDto(dto)
        }
        verify(exactly = 1) { modelService.findByIdOrNull(dto.id!!) }
    }

    @DisplayName("DELETE existing entity performs with 200")
    @Test
    fun givenExistingId_whenDelete_thenReturn200() {
        // GIVEN
        val id = randomUUID()
        every { modelService.deleteById(id) } just Runs
        // WHEN-THEN
        val url = BASE_PATH_MODELS + PATH_MODEL_ID
        mockMvc.delete(url, id).andExpect {
            status { isOk() }
        }
        verify(exactly = 1) { modelService.deleteById(id) }
    }

    private fun createShortView(name: String): ModelShortView {
        val projection = projectionFactory.createProjection(ModelShortView::class.java)
        projection.id = randomUUID()
        projection.name = name
        return projection
    }

    private fun createModelFullDto(): ModelFullDto {
        val activities = mutableListOf(
            ModelActivityDto(randomUUID(), "Activity Foo", impacts = null),
            ModelActivityDto(randomUUID(), "Activity Bar", impacts = null),
        )
        val resources = mutableListOf(
            ModelResourceDto(randomUUID(), "Resource Foo", 100),
            ModelResourceDto(randomUUID(), "Resource Bar", 500),
        )
        return ModelFullDto(randomUUID(), "Model name", resources, activities)
    }

    private fun MockMvcResultMatchersDsl.assertContainsModelFullDto(dto: ModelFullDto) {
        content {
            contentType(MediaType.APPLICATION_JSON)
            jsonPath("$.id") { value(dto.id.toString()) }
            jsonPath("$.name") { value(dto.name) }
            dto.activities!!.forEachIndexed { index, activityDto ->
                jsonPath("$.activities[$index].activityId") { value(activityDto.activityId.toString()) }
                jsonPath("$.activities[$index].name") { value(activityDto.name) }
            }
            dto.resources!!.forEachIndexed { index, resourceDto ->
                jsonPath("$.resources[$index].resourceId") { value(resourceDto.resourceId.toString()) }
                jsonPath("$.resources[$index].name") { value(resourceDto.name) }
                jsonPath("$.resources[$index].amount") { value(resourceDto.amount?.toInt()) }
            }
        }
    }
}
