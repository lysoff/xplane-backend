package app.xplne.api.repository

import app.xplne.api.model.Model
import app.xplne.api.repository.common.CustomJpaRepository
import java.util.*

interface ModelRepository: CustomJpaRepository<Model, UUID>