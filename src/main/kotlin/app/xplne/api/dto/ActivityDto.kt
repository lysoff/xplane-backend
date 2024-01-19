package app.xplne.api.dto

import app.xplne.api.dto.scope.Basic
import app.xplne.api.dto.scope.OnCreate
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Size
import java.util.*

data class ActivityDto(
    @field:Null(groups = [OnCreate::class])
    var id: UUID?,

    @field:NotBlank(groups = [Basic::class])
    @field:Size(min = 1, max = 128, groups = [Basic::class])
    val name: String?
)
