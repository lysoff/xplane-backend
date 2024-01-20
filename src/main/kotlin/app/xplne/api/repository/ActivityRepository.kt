package app.xplne.api.repository

import app.xplne.api.model.Activity
import app.xplne.api.repository.common.CustomJpaRepository
import java.util.*

interface ActivityRepository: CustomJpaRepository<Activity, UUID>