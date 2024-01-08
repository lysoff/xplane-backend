package app.xplne.api.repository

import app.xplne.api.model.Resource
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ResourceRepository: JpaRepository<Resource, UUID>