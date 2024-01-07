package app.xplne.api.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
data class Model(
    @Id @UuidGenerator
    var id: UUID? = null,
    val name: String,
)
