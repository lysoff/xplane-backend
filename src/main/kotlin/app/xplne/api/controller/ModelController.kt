package app.xplne.api.controller

import app.xplne.api.dto.ModelFullDto
import app.xplne.api.dto.ModelShortView
import app.xplne.api.service.ModelService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/models")
@Tag(name = "Models")
class ModelController(
    private val modelService: ModelService
) {

    @GetMapping
    @Operation(summary = "Get all models")
    fun findAllModels(): List<ModelShortView> {
        return modelService.findAll()
    }

    @GetMapping("/{modelId}")
    @Operation(summary = "Get model by ID")
    fun getModelById(@PathVariable modelId: UUID): ModelFullDto? {
        return modelService.findByIdOrNull(modelId)
    }

    @DeleteMapping("/{modelId}")
    @Operation(summary = "Delete Model")
    fun deleteModel(@PathVariable modelId: UUID) {
        modelService.deleteById(modelId)
    }
}