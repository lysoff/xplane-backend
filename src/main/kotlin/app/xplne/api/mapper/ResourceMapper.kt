package app.xplne.api.mapper

import app.xplne.api.dto.ResourceDto
import app.xplne.api.model.Resource
import org.mapstruct.Mapper

@Mapper
interface ResourceMapper {
    fun toDto(resource: Resource?): ResourceDto?
    fun toEntity(resourceDto: ResourceDto): Resource
}