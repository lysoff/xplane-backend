package app.xplne.backend.model

import org.springframework.data.annotation.Id
import java.util.UUID

data class Resource(
    @Id var id: UUID? = null,
    val name: String
)
