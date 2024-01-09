package app.xplne.api.repository

import app.xplne.api.model.Activity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ActivityRepository: JpaRepository<Activity, UUID>