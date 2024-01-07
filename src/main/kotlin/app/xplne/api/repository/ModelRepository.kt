package app.xplne.api.repository

import app.xplne.api.model.Model
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ModelRepository: JpaRepository<Model, UUID>