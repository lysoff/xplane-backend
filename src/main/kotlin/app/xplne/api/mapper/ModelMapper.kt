package app.xplne.api.mapper

import app.xplne.api.dto.ModelFullDto
import app.xplne.api.model.Model
import org.mapstruct.Mapper

@Mapper(uses = [ModelActivityMapper::class, ModelResourceMapper::class])
interface ModelMapper {

    fun toFullDto(entity: Model): ModelFullDto
}