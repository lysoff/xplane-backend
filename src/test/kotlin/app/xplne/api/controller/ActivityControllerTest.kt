package app.xplne.api.controller

import app.xplne.api.constants.BASE_PATH_ACTIVITY
import app.xplne.api.constants.PATH_ACTIVITY_ID
import app.xplne.api.dto.ActivityDto
import app.xplne.api.exception.NotFoundException
import app.xplne.api.service.ActivityService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import java.util.*


@WebMvcTest(ActivityController::class)
@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class ActivityControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var activityService: ActivityService

    private var mapper = ObjectMapper()

    @Test
    fun givenTwoActivities_whenGetAll_thenReturnAllWith200() {
        // GIVEN
        val activities = listOf(
            ActivityDto(UUID.randomUUID(), "Activity 1"),
            ActivityDto(UUID.randomUUID(), "Activity 2"))
        val paged: Slice<ActivityDto> = SliceImpl(activities, Pageable.ofSize(10), false)

        every { activityService.findAll(any<Pageable>()) } returns paged
        // WHEN-THEN
        mockMvc.get(BASE_PATH_ACTIVITY) {
            param("size", paged.size.toString())
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                assertJsonHasPageData(paged)
                activities.forEachIndexed { index, dto ->
                    jsonPath("$.content[$index].id") { value(dto.id.toString()) }
                    jsonPath("$.content[$index].name") { value(dto.name) }
                }
            }
        }
        verify(exactly = 1) { activityService.findAll(any<Pageable>()) }
    }

    @Test
    fun givenNewActivity_whenPostCreate_thenReturnItWith200() {
        // GIVEN
        val dto = ActivityDto(UUID.randomUUID(), "Activity name")
        every { activityService.create(any<ActivityDto>()) } returns dto
        // WHEN-THEN
        mockMvc.post(BASE_PATH_ACTIVITY) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(dto)
        }.andExpect {
            status { isOk() }
            assertContainsActivityDto(dto)
        }
        verify(exactly = 1) { activityService.create(any<ActivityDto>()) }
    }

    @Test
    fun givenExistingActivity_whenPutUpdate_thenReturnItWith200() {
        // GIVEN
        val dto = ActivityDto(UUID.randomUUID(), "Activity name")
        every { activityService.update(any<ActivityDto>()) } returns dto
        // WHEN-THEN
        val url = BASE_PATH_ACTIVITY + PATH_ACTIVITY_ID
        mockMvc.put(url, dto.id) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(dto)
        }.andExpect {
            status { isOk() }
            assertContainsActivityDto(dto)
        }
        verify(exactly = 1) { activityService.update(any<ActivityDto>()) }
    }

    @Test
    fun givenActivityDoesNotExist_whenPutUpdate_thenReturn404() {
        // GIVEN
        val dto = ActivityDto(UUID.randomUUID(), "Not existing activity")
        every { activityService.update(any<ActivityDto>()) } throws NotFoundException("Error")
        // WHEN-THEN
        val url = BASE_PATH_ACTIVITY + PATH_ACTIVITY_ID
        mockMvc.put(url, dto.id) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(dto)
        }.andExpect {
            status { isNotFound() }
        }
        verify(exactly = 1) { activityService.update(any<ActivityDto>()) }
    }

    @Test
    fun givenExistingId_whenGetById_thenReturnItWith200() {
        // GIVEN
        val dto = ActivityDto(UUID.randomUUID(), "Activity name")
        every { activityService.findByIdOrNull(dto.id!!) } returns dto
        // WHEN-THEN
        val url = BASE_PATH_ACTIVITY + PATH_ACTIVITY_ID
        mockMvc.get(url, dto.id).andExpect {
            status { isOk() }
            assertContainsActivityDto(dto)
        }
        verify(exactly = 1) { activityService.findByIdOrNull(dto.id!!) }
    }

    @Test
    fun givenExistingId_whenDelete_thenReturn200() {
        // GIVEN
        val id = UUID.randomUUID()
        every { activityService.deleteById(id) } just Runs
        // WHEN-THEN
        val url = BASE_PATH_ACTIVITY + PATH_ACTIVITY_ID
        mockMvc.delete(url, id).andExpect {
            status { isOk() }
        }
        verify(exactly = 1) { activityService.deleteById(id) }
    }

    private fun <T> MockMvcResultMatchersDsl.assertJsonHasPageData(page: Slice<T>) {
        jsonPath("$.size") { value(page.size) }
        jsonPath("$.first") { value(page.isFirst) }
        jsonPath("$.last") { value(page.isLast) }
        jsonPath("$.pageable.pageSize") { value(page.size) }
        jsonPath("$.content.length()") { page.content.size }
    }

    private fun MockMvcResultMatchersDsl.assertContainsActivityDto(dto: ActivityDto) {
        content {
            contentType(MediaType.APPLICATION_JSON)
            jsonPath("$.id") { value(dto.id.toString()) }
            jsonPath("$.name") { value(dto.name) }
        }
    }
}
