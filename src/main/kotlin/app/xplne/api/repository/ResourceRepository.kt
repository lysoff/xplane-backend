package app.xplne.api.repository

import app.xplne.api.model.Resource
import app.xplne.api.repository.common.CustomJpaRepository
import java.util.*

interface ResourceRepository: CustomJpaRepository<Resource, UUID>