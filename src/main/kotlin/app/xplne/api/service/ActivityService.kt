package app.xplne.api.service

import app.xplne.api.dto.ActivityDto
import app.xplne.api.exception.NotFoundException
import app.xplne.api.mapper.ActivityMapper
import app.xplne.api.repository.ActivityRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class ActivityService(
    private val activityRepository: ActivityRepository
) {
    private val mapper = Mappers.getMapper(ActivityMapper::class.java)

    fun findAll(pageable: Pageable): Slice<ActivityDto> {
        return activityRepository.findAll(pageable)
            .map(mapper::toDto)
    }

    fun findByIdOrNull(activityId: UUID): ActivityDto? {
        return activityRepository.findById(activityId)
            .getOrNull()
            .run(mapper::toDto)
    }

    fun create(dto: ActivityDto): ActivityDto {
        return activityRepository.persist(mapper.toEntity(dto))
            .run(mapper::toDto)!!
    }

    fun update(dto: ActivityDto): ActivityDto {
        val entity = mapper.toEntity(dto)
        val updated = try {
            activityRepository.update(entity)
        } catch (ex: ObjectOptimisticLockingFailureException) {
            throw NotFoundException("Entity with provided does not exist", ex)
        }
        return updated.run(mapper::toDto)!!
    }

    fun deleteById(activityId: UUID) {
        activityRepository.deleteById(activityId)
    }
}