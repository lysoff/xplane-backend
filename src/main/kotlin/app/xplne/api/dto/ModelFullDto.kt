package app.xplne.api.dto

import java.util.*

data class ModelFullDto (
    val id: UUID?,
    val name: String?,
    val resources: MutableList<ModelResourceDto>?,
    val activities: MutableList<ModelActivityDto>?,
)
