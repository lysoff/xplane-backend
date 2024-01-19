package app.xplne.api.controller

import app.xplne.api.dto.ModelFullDto
import app.xplne.api.dto.ModelShortView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/models")
@Tag(name = "Models")
class ModelController {

    @GetMapping
    @Operation(summary = "Get all models")
    fun findAllModels(): List<ModelShortView> {
        // TODO implement interaction with DB
        return emptyList()
    }

    @GetMapping("/{modelId}")
    @Operation(summary = "Get model by ID")
    fun getModelById(@PathVariable modelId: UUID): ModelFullDto? {
        // TODO implement interaction with DB
        return null
    }

    @DeleteMapping("/{modelId}")
    @Operation(summary = "Delete Model")
    fun deleteModel(@PathVariable modelId: UUID) {
        // TODO implement interaction with DB
    }
}