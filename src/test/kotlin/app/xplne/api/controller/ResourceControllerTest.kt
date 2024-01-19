package app.xplne.api.controller

import app.xplne.api.constants.BASE_PATH_RESOURCES
import app.xplne.api.constants.PATH_RESOURCE_ID
import app.xplne.api.dto.ResourceDto
import app.xplne.api.exception.NotFoundException
import app.xplne.api.service.ResourceService
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


@WebMvcTest(ResourceController::class)
@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class ResourceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var resourceService: ResourceService

    private var mapper = ObjectMapper()

    @Test
    fun givenTwoResources_whenGetAll_thenReturnAllWith200() {
        // GIVEN
        val resources = listOf(
            ResourceDto(UUID.randomUUID(), "Resource 1"),
            ResourceDto(UUID.randomUUID(), "Resource 2"))
        val paged: Slice<ResourceDto> = SliceImpl(resources, Pageable.ofSize(10), false)

        every { resourceService.findAll(any<Pageable>()) } returns paged
        // WHEN-THEN
        mockMvc.get(BASE_PATH_RESOURCES) {
            param("size", paged.size.toString())
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                assertJsonHasPageData(paged)
                resources.forEachIndexed { index, dto ->
                    jsonPath("$.content[$index].id") { value(dto.id.toString()) }
                    jsonPath("$.content[$index].name") { value(dto.name) }
                }
            }
        }
        verify(exactly = 1) { resourceService.findAll(any<Pageable>()) }
    }

    @Test
    fun givenNewResource_whenPostCreate_thenReturnItWith200() {
        // GIVEN
        val dto = ResourceDto(UUID.randomUUID(), "Resource name")
        every { resourceService.create(any<ResourceDto>()) } returns dto
        // WHEN-THEN
        mockMvc.post(BASE_PATH_RESOURCES) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(dto)
        }.andExpect {
            status { isOk() }
            assertContainsResourceDto(dto)
        }
        verify(exactly = 1) { resourceService.create(any<ResourceDto>()) }
    }

    @Test
    fun givenExistingResource_whenPutUpdate_thenReturnItWith200() {
        // GIVEN
        val dto = ResourceDto(UUID.randomUUID(), "Resource name")
        every { resourceService.update(any<ResourceDto>()) } returns dto
        // WHEN-THEN
        val url = BASE_PATH_RESOURCES + PATH_RESOURCE_ID
        mockMvc.put(url, dto.id) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(dto)
        }.andExpect {
            status { isOk() }
            assertContainsResourceDto(dto)
        }
        verify(exactly = 1) { resourceService.update(any<ResourceDto>()) }
    }

    @Test
    fun givenResourceDoesNotExist_whenPutUpdate_thenReturn404() {
        // GIVEN
        val dto = ResourceDto(UUID.randomUUID(), "Not existing resource")
        every { resourceService.update(any<ResourceDto>()) } throws NotFoundException("Error")
        // WHEN-THEN
        val url = BASE_PATH_RESOURCES + PATH_RESOURCE_ID
        mockMvc.put(url, dto.id) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(dto)
        }.andExpect {
            status { isNotFound() }
        }
        verify(exactly = 1) { resourceService.update(any<ResourceDto>()) }
    }

    @Test
    fun givenExistingId_whenGetById_thenReturnItWith200() {
        // GIVEN
        val dto = ResourceDto(UUID.randomUUID(), "Resource name")
        every { resourceService.findByIdOrNull(dto.id!!) } returns dto
        // WHEN-THEN
        val url = BASE_PATH_RESOURCES + PATH_RESOURCE_ID
        mockMvc.get(url, dto.id).andExpect {
            status { isOk() }
            assertContainsResourceDto(dto)
        }
        verify(exactly = 1) { resourceService.findByIdOrNull(dto.id!!) }
    }

    @Test
    fun givenExistingId_whenDelete_thenReturn200() {
        // GIVEN
        val id = UUID.randomUUID()
        every { resourceService.deleteById(id) } just Runs
        // WHEN-THEN
        val url = BASE_PATH_RESOURCES + PATH_RESOURCE_ID
        mockMvc.delete(url, id).andExpect {
            status { isOk() }
        }
        verify(exactly = 1) { resourceService.deleteById(id) }
    }

    private fun <T> MockMvcResultMatchersDsl.assertJsonHasPageData(page: Slice<T>) {
        jsonPath("$.size") { value(page.size) }
        jsonPath("$.first") { value(page.isFirst) }
        jsonPath("$.last") { value(page.isLast) }
        jsonPath("$.pageable.pageSize") { value(page.size) }
        jsonPath("$.content.length()") { page.content.size }
    }

    private fun MockMvcResultMatchersDsl.assertContainsResourceDto(dto: ResourceDto) {
        content {
            contentType(MediaType.APPLICATION_JSON)
            jsonPath("$.id") { value(dto.id.toString()) }
            jsonPath("$.name") { value(dto.name) }
        }
    }
}
