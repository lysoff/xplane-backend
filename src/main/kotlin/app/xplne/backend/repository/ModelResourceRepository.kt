package app.xplne.backend.repository

import app.xplne.backend.model.ModelResource
import app.xplne.backend.model.ModelResource.ModelResourcePK
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ModelResourceRepository: ReactiveCrudRepository<ModelResource, ModelResourcePK> {
    @Query("""
        INSERT INTO model_resource(model_id, resource_id, amount) 
        VALUES (:#{#modelResource.modelId}, :#{#modelResource.resourceId}, :#{#modelResource.amount})
        ON CONFLICT ON CONSTRAINT model_resource_pkey DO UPDATE SET amount = :#{#modelResource.amount}
    """)
    fun upsert(modelResource: ModelResource): Mono<ModelResource>

    fun findAllByModelId(modelId: UUID): Flux<ModelResource>

    fun deleteByModelIdAndResourceId(modelId: UUID, resourceId: UUID): Mono<Void>
}