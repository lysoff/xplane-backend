package app.xplne.api.dto

import java.util.*

data class  ModelActivityDto (
    val activityId: UUID?,
    val name: String?,
    val impacts: List<ActivityImpactDto>?,
)
