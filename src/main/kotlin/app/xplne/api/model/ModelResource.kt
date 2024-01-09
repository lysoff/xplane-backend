package app.xplne.api.model

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
data class ModelResource(
    @EmbeddedId
    val id: ModelResourceKey? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("modelId")
    @JoinColumn(name = "model_id")
    val model: Model,
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    val resource: Resource,
    val amount: Short
) {
    @Embeddable
    data class ModelResourceKey(
        var modelId: UUID? = null,
        var resourceId: UUID? = null
    ) : Serializable

    constructor(model: Model, resource: Resource, amount: Short) :
            this(ModelResourceKey(model.id, resource.id), model, resource, amount)

    override fun toString(): String {
        return "ModelResource(model_id=${model.id}, resource=${resource}, amount=$amount)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelResource

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
    
}
