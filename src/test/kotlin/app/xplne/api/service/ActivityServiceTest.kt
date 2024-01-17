package app.xplne.api.service

import app.xplne.api.dto.ActivityDto
import app.xplne.api.mapper.ActivityMapper
import app.xplne.api.model.Activity
import app.xplne.api.repository.ActivityRepository
import app.xplne.api.repository.common.findByIdOrNull
import app.xplne.api.util.TestData
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.*
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class ActivityServiceTest {

    @MockK
    private lateinit var activityRepository: ActivityRepository

    @MockK
    private lateinit var activityMapper: ActivityMapper

    @InjectMockKs
    private lateinit var activityService: ActivityService

    @BeforeEach
    fun initialize() {
        ReflectionTestUtils.setField(activityService, "mapper", activityMapper)
    }

    @Test
    fun findAll() {
        // GIVEN
        val pageable = Pageable.ofSize(10)
        val activitiesList = TestData.getBasicActivities()
        val pageOfActivities = PageImpl(activitiesList)
        every { activityRepository.findAll(pageable) } returns pageOfActivities
        every { activityMapper.toDto(any(Activity::class)) } returns mockkClass(ActivityDto::class)
        // WHEN
        val result: Slice<ActivityDto> = activityService.findAll(pageable)
        // THEN
        assertFalse(result.isEmpty)
        verify(exactly = 1) { activityRepository.findAll(pageable) }
        verify(exactly = activitiesList.size) { activityMapper.toDto(any()) }
    }

    @Test
    fun findByIdOrNull() {
        // GIVEN
        val id = UUID.randomUUID()
        val activityDto = mockkClass(ActivityDto::class)
        val activity = mockkClass(Activity::class)
        every { activityRepository.findByIdOrNull(id) } returns activity
        every { activityMapper.toDto(activity) } returns activityDto
        // WHEN
        val result: ActivityDto? = activityService.findByIdOrNull(id)
        // THEN
        assertNotNull(result)
        verify(exactly = 1) { activityRepository.findByIdOrNull(id) }
        verify(exactly = 1) { activityMapper.toDto(activity) }
    }

    @Test
    fun create() {
        // GIVEN
        val activityDto = mockkClass(ActivityDto::class)
        val activity = mockkClass(Activity::class)
        every { activityMapper.toEntity(activityDto) } returns activity
        every { activityRepository.persist(activity) } returns activity
        every { activityMapper.toDto(activity) } returns activityDto
        // WHEN
        val result: ActivityDto = activityService.create(activityDto)
        // THEN
        assertNotNull(result)
        verify(exactly = 1) { activityMapper.toEntity(activityDto) }
        verify(exactly = 1) { activityRepository.persist(activity) }
        verify(exactly = 1) { activityMapper.toDto(activity) }
    }

    @Test
    fun update() {
        // GIVEN
        val activityDto = mockkClass(ActivityDto::class)
        val activity = mockkClass(Activity::class)
        every { activityMapper.toEntity(activityDto) } returns activity
        every { activityRepository.update(activity) } returns activity
        every { activityMapper.toDto(activity) } returns activityDto
        // WHEN
        val result: ActivityDto = activityService.update(activityDto)
        // THEN
        assertNotNull(result)
        verify(exactly = 1) { activityMapper.toEntity(activityDto) }
        verify(exactly = 1) { activityRepository.update(activity) }
        verify(exactly = 1) { activityMapper.toDto(activity) }
    }

    @Test
    fun deleteById() {
        // GIVEN
        val id = UUID.randomUUID()
        every { activityRepository.deleteById(id) } just Runs
        // WHEN
        activityService.deleteById(id)
        // THEN
        verify(exactly = 1) { activityRepository.deleteById(id) }
    }
}