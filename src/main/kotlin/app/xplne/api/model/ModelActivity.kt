package app.xplne.api.model

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
data class ModelActivity(
    @EmbeddedId
    val id: ModelActivityKey? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("modelId")
    @JoinColumn(name = "model_id")
    val model: Model,

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("activityId")
    @JoinColumn(name = "activity_id")
    val activity: Activity
) {
    @Embeddable
    data class ModelActivityKey(
        var modelId: UUID? = null,
        var activityId: UUID? = null
    ) : Serializable

    constructor(model: Model, activity: Activity) :
            this(ModelActivityKey(model.id, activity.id), model, activity)

    override fun toString(): String {
        return "ModelActivity(model_id=${model.id}, activity=${activity})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelActivity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
