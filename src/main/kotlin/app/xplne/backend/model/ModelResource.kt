package app.xplne.backend.model

import java.util.*

data class ModelResource(
    val modelId: UUID,
    val resourceId: UUID,
    val amount: Short
) {
    data class ModelResourcePK(
        val modelId: UUID,
        val resourceId: UUID
    )
}
