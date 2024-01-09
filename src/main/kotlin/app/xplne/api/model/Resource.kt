package app.xplne.api.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
data class Resource(
    @Id @UuidGenerator
    var id: UUID? = null,
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resource

        return id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
