package app.xplne.api.controller

import app.xplne.api.constants.BASE_PATH_MODELS
import app.xplne.api.constants.PATH_MODEL_ID
import app.xplne.api.dto.ModelFullDto
import app.xplne.api.dto.ModelShortView
import app.xplne.api.service.ModelService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(BASE_PATH_MODELS)
@Tag(name = "Models")
class ModelController(
    private val modelService: ModelService
) {

    @GetMapping
    @Operation(summary = "Get all models")
    fun findAllModels(): List<ModelShortView> {
        return modelService.findAll()
    }

    @GetMapping(PATH_MODEL_ID)
    @Operation(summary = "Get model by ID")
    fun getModelById(@PathVariable modelId: UUID): ModelFullDto? {
        return modelService.findByIdOrNull(modelId)
    }

    @DeleteMapping(PATH_MODEL_ID)
    @Operation(summary = "Delete Model")
    fun deleteModel(@PathVariable modelId: UUID) {
        modelService.deleteById(modelId)
    }
}