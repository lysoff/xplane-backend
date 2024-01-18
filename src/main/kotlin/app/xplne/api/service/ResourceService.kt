package app.xplne.api.service

import app.xplne.api.dto.ResourceDto
import app.xplne.api.mapper.ResourceMapper
import app.xplne.api.repository.ResourceRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class ResourceService(
    private val resourceRepository: ResourceRepository
) {
    private val mapper = Mappers.getMapper(ResourceMapper::class.java)

    fun findAll(pageable: Pageable): Slice<ResourceDto> {
        return resourceRepository.findAll(pageable)
            .map(mapper::toDto)
    }

    fun findByIdOrNull(resourceId: UUID): ResourceDto? {
        return resourceRepository.findById(resourceId)
            .getOrNull()
            .run(mapper::toDto)
    }

    fun create(dto: ResourceDto): ResourceDto {
        return resourceRepository.persist(mapper.toEntity(dto))
            .run(mapper::toDto)!!
    }

    fun update(dto: ResourceDto): ResourceDto {
        return resourceRepository.update(mapper.toEntity(dto))
            .run(mapper::toDto)!!
    }

    fun deleteById(resourceId: UUID) {
        resourceRepository.deleteById(resourceId)
    }
}