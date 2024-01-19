package app.xplne.api.repository

import app.xplne.api.dto.ModelShortView
import app.xplne.api.model.Model
import io.hypersistence.utils.spring.repository.BaseJpaRepository
import java.util.*

interface ModelRepository: BaseJpaRepository<Model, UUID> {
    fun findAllBy(): List<ModelShortView>
}