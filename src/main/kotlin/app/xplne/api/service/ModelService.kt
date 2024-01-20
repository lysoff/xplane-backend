package app.xplne.api.service

import app.xplne.api.dto.ModelFullDto
import app.xplne.api.dto.ModelShortView
import app.xplne.api.mapper.ModelMapper
import app.xplne.api.model.Model
import app.xplne.api.repository.ModelRepository
import app.xplne.api.repository.common.findByIdOrNull
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.util.*

@Service
class ModelService(
    private val modelRepository: ModelRepository
) {
    private val modelMapper = Mappers.getMapper(ModelMapper::class.java)

    fun findAll(): List<ModelShortView> {
        return modelRepository.findAllBy()
    }

    fun findByIdOrNull(modelId: UUID): ModelFullDto? {
        val model: Model = modelRepository.findByIdOrNull(modelId)
            ?: return null
        return modelMapper.toFullDto(model)
    }

    fun deleteById(modelId: UUID) {
        modelRepository.deleteById(modelId)
    }
}