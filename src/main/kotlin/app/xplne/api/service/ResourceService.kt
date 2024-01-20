package app.xplne.api.service

import app.xplne.api.constants.ENTITY_NOT_FOUND
import app.xplne.api.dto.ResourceDto
import app.xplne.api.exception.NotFoundException
import app.xplne.api.mapper.ResourceMapper
import app.xplne.api.repository.ResourceRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.orm.ObjectOptimisticLockingFailureException
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
        val entity = mapper.toEntity(dto)
        val updated = try {
            resourceRepository.update(entity)
        } catch (ex: ObjectOptimisticLockingFailureException) {
            throw NotFoundException(ENTITY_NOT_FOUND, ex)
        }
        return updated.run(mapper::toDto)!!
    }

    fun deleteById(resourceId: UUID) {
        resourceRepository.deleteById(resourceId)
    }
}