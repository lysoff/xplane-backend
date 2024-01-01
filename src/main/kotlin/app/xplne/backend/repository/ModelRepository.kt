package app.xplne.backend.repository

import app.xplne.backend.model.Model
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface ModelRepository: ReactiveCrudRepository<Model, UUID>