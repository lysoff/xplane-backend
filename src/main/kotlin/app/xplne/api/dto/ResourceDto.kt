package app.xplne.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class ResourceDto(
    var id: UUID?,
    @NotBlank @Size(min = 1, max = 128)
    val name: String
)
