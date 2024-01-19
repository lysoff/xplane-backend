package app.xplne.api.mapper

import app.xplne.api.dto.ModelActivityDto
import app.xplne.api.model.ModelActivity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface ModelActivityMapper {
    @Mapping(target = "name", source = "activity.name")
    @Mapping(target = "activityId", source = "activity.id")
    fun toDto(entity: ModelActivity): ModelActivityDto
}