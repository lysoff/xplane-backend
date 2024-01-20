package app.xplne.api.service

import app.xplne.api.dto.ModelFullDto
import app.xplne.api.dto.ModelShortView
import app.xplne.api.mapper.ModelMapper
import app.xplne.api.model.Model
import app.xplne.api.repository.ModelRepository
import app.xplne.api.repository.common.findByIdOrNull
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class ModelServiceTest {

    @MockK
    private lateinit var modelRepository: ModelRepository

    @MockK
    private lateinit var modelMapper: ModelMapper

    @InjectMockKs
    private lateinit var modelService: ModelService

    @BeforeEach
    fun initialize() {
        ReflectionTestUtils.setField(modelService, "modelMapper", modelMapper)
    }

    @Test
    fun givenModels_whenFindAll_thenReturnListFromRepo() {
        // GIVEN
        val repoOutput: List<ModelShortView> = mockModelShortViews()
        every { modelRepository.findAllBy() } returns repoOutput
        // WHEN
        val serviceOutput: List<ModelShortView> = modelService.findAll()
        // THEN
        assertTrue(repoOutput === serviceOutput)
        verify(exactly = 1) { modelRepository.findAllBy() }
    }

    @Test
    fun givenExistingModel_whenFindById_thenReturnDtoFromRepo() {
        // GIVEN
        val id = UUID.randomUUID()
        val model = mockkClass(Model::class)
        val mapperOutput = mockkClass(ModelFullDto::class)
        every { modelRepository.findByIdOrNull(id) } returns model
        every { modelMapper.toFullDto(model) } returns mapperOutput
        // WHEN
        val serviceOutput: ModelFullDto? = modelService.findByIdOrNull(id)
        // THEN
        assertTrue(mapperOutput === serviceOutput)
        verify(exactly = 1) { modelRepository.findByIdOrNull(id) }
        verify(exactly = 1) { modelMapper.toFullDto(model) }
    }

    @Test
    fun givenExistingModel_whenDeleteById_thenCallRepo() {
        // GIVEN
        val id = UUID.randomUUID()
        every { modelRepository.deleteById(id) } just Runs
        // WHEN
        modelService.deleteById(id)
        // THEN
        verify(exactly = 1) { modelRepository.deleteById(id) }
    }

    private fun mockModelShortViews(): List<ModelShortView> {
        val firstModel = mockk<ModelShortView>(relaxed = true)
        val secondModel = mockk<ModelShortView>(relaxed = true)
        return listOf(firstModel, secondModel)
    }
}