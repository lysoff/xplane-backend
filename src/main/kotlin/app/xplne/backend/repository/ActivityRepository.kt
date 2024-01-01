package app.xplne.backend.repository

import app.xplne.backend.model.Activity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface ActivityRepository: ReactiveCrudRepository<Activity, UUID>