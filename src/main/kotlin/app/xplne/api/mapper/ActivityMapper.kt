package app.xplne.api.mapper

import app.xplne.api.dto.ActivityDto
import app.xplne.api.model.Activity
import org.mapstruct.Mapper

@Mapper
interface ActivityMapper {
    fun toDto(activity: Activity?): ActivityDto?
    fun toEntity(activityDto: ActivityDto): Activity
}