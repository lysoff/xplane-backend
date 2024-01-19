package app.xplne.api.mapper

import app.xplne.api.dto.ModelResourceDto
import app.xplne.api.model.ModelResource
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface ModelResourceMapper {
    @Mapping(target = "name", source = "resource.name")
    @Mapping(target = "resourceId", source = "resource.id")
    fun toDto(entity: ModelResource): ModelResourceDto
}