package app.xplne.api.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
data class Model(
    @Id @UuidGenerator
    var id: UUID? = null,

    val name: String,

    @OneToMany(
        mappedBy = "model",
        cascade = [CascadeType.ALL],
        orphanRemoval = true)
    var resources: MutableList<ModelResource> = mutableListOf(),

    @OneToMany(
        mappedBy = "model",
        cascade = [CascadeType.ALL],
        orphanRemoval = true)
    var activities: MutableList<ModelActivity> = mutableListOf()
) {
    fun addResource(resource: Resource, amount: Short) {
        val modelResource = ModelResource(this, resource, amount)
        resources.add(modelResource)
    }

    fun addActivity(activity: Activity) {
        val modelActivity = ModelActivity(this, activity)
        activities.add(modelActivity)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Model

        return id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
