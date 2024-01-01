package app.xplne.backend.repository

import app.xplne.backend.model.Resource
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface ResourceRepository: ReactiveCrudRepository<Resource, UUID>