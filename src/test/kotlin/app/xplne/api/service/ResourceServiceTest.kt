package app.xplne.api.service

import app.xplne.api.dto.ResourceDto
import app.xplne.api.exception.NotFoundException
import app.xplne.api.mapper.ResourceMapper
import app.xplne.api.model.Resource
import app.xplne.api.repository.ResourceRepository
import app.xplne.api.repository.common.findByIdOrNull
import app.xplne.api.util.TestData
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class ResourceServiceTest {

    @MockK
    private lateinit var resourceRepository: ResourceRepository

    @MockK
    private lateinit var resourceMapper: ResourceMapper

    @InjectMockKs
    private lateinit var resourceService: ResourceService

    @BeforeEach
    fun initialize() {
        ReflectionTestUtils.setField(resourceService, "mapper", resourceMapper)
    }

    @Test
    fun givenResources_whenFindAll_thenReturnPageFromRepo() {
        // GIVEN
        val pageable = Pageable.ofSize(10)
        val resourcesList = TestData.getBasicResources()
        val pageOfResources = PageImpl(resourcesList)
        every { resourceRepository.findAll(pageable) } returns pageOfResources
        every { resourceMapper.toDto(any(Resource::class)) } returns mockkClass(ResourceDto::class)
        // WHEN
        val result: Slice<ResourceDto> = resourceService.findAll(pageable)
        // THEN
        assertFalse(result.isEmpty)
        verify(exactly = 1) { resourceRepository.findAll(pageable) }
        verify(exactly = resourcesList.size) { resourceMapper.toDto(any()) }
    }

    @Test
    fun givenExistingResource_whenFindById_thenReturnDtoFromRepo() {
        // GIVEN
        val id = UUID.randomUUID()
        val resourceDto = mockkClass(ResourceDto::class)
        val resource = mockkClass(Resource::class)
        every { resourceRepository.findByIdOrNull(id) } returns resource
        every { resourceMapper.toDto(resource) } returns resourceDto
        // WHEN
        val result: ResourceDto? = resourceService.findByIdOrNull(id)
        // THEN
        assertNotNull(result)
        verify(exactly = 1) { resourceRepository.findByIdOrNull(id) }
        verify(exactly = 1) { resourceMapper.toDto(resource) }
    }

    @Test
    fun givenNewResource_whenCreate_thenReturnRepoResult() {
        // GIVEN
        val resourceDto = mockkClass(ResourceDto::class)
        val resource = mockkClass(Resource::class)
        every { resourceMapper.toEntity(resourceDto) } returns resource
        every { resourceRepository.persist(resource) } returns resource
        every { resourceMapper.toDto(resource) } returns resourceDto
        // WHEN
        val result: ResourceDto = resourceService.create(resourceDto)
        // THEN
        assertNotNull(result)
        verify(exactly = 1) { resourceMapper.toEntity(resourceDto) }
        verify(exactly = 1) { resourceRepository.persist(resource) }
        verify(exactly = 1) { resourceMapper.toDto(resource) }
    }

    @Test
    fun givenExistingResource_whenUpdate_thenReturnRepoResult() {
        // GIVEN
        val resourceDto = mockkClass(ResourceDto::class)
        val resource = mockkClass(Resource::class)
        every { resourceMapper.toEntity(resourceDto) } returns resource
        every { resourceRepository.update(resource) } returns resource
        every { resourceMapper.toDto(resource) } returns resourceDto
        // WHEN
        val result: ResourceDto = resourceService.update(resourceDto)
        // THEN
        assertNotNull(result)
        verify(exactly = 1) { resourceMapper.toEntity(resourceDto) }
        verify(exactly = 1) { resourceRepository.update(resource) }
        verify(exactly = 1) { resourceMapper.toDto(resource) }
    }

    @Test
    fun givenResourceDoesNotExist_whenUpdate_thenReturnRepoResult() {
        // GIVEN
        val resourceDto = mockkClass(ResourceDto::class)
        val resource = mockkClass(Resource::class)
        every { resourceMapper.toEntity(resourceDto) } returns resource
        every { resourceRepository.update(resource) } throws
                ObjectOptimisticLockingFailureException(Resource::class.java, UUID.randomUUID())
        // WHEN-THEN
        assertThrows<NotFoundException> {
            resourceService.update(resourceDto)
        }
        verify(exactly = 1) { resourceMapper.toEntity(resourceDto) }
        verify(exactly = 1) { resourceRepository.update(resource) }
    }

    @Test
    fun givenExistingResource_whenDeleteById_thenCallRepo() {
        // GIVEN
        val id = UUID.randomUUID()
        every { resourceRepository.deleteById(id) } just Runs
        // WHEN
        resourceService.deleteById(id)
        // THEN
        verify(exactly = 1) { resourceRepository.deleteById(id) }
    }
}