package app.xplne.api.controller

import app.xplne.api.constants.*
import app.xplne.api.dto.ErrorResponseDto
import app.xplne.api.dto.ResourceDto
import app.xplne.api.dto.scope.Basic
import app.xplne.api.dto.scope.OnCreate
import app.xplne.api.service.ResourceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(BASE_PATH_RESOURCES)
@Tag(name = "Resources")
class ResourceController(
    private val resourceService: ResourceService
) {

    @GetMapping
    @Operation(summary = "Find all resources")
    fun findAllResources(@ParameterObject pageable: Pageable): Slice<ResourceDto> =
        resourceService.findAll(pageable)

    @PostMapping
    @Operation(summary = "Create resource")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = ENTITY_UPDATED),
        ApiResponse(responseCode = "400", description = DTO_VALIDATION_FAILED,
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]),
        ApiResponse(responseCode = "404", description = ENTITY_NOT_FOUND, content = [Content()]),
    )
    fun createResource(
        @RequestBody @Validated(OnCreate::class) dto: ResourceDto
    ): ResourceDto =
        resourceService.create(dto)

    @PutMapping(PATH_RESOURCE_ID)
    @Operation(summary = "Update resource")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = ENTITY_UPDATED),
        ApiResponse(responseCode = "400", description = DTO_VALIDATION_FAILED,
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]),
        ApiResponse(responseCode = "404", description = ENTITY_NOT_FOUND, content = [Content()]),
    )
    fun updateResource(
        @PathVariable resourceId: UUID,
        @RequestBody @Validated(Basic::class) dto: ResourceDto
    ): ResourceDto {
        dto.id = resourceId
        return resourceService.update(dto)
    }

    @GetMapping(PATH_RESOURCE_ID)
    @Operation(summary = "Get resource by ID")
    fun getResourceById(@PathVariable resourceId: UUID): ResourceDto? =
        resourceService.findByIdOrNull(resourceId)

    @DeleteMapping(PATH_RESOURCE_ID)
    @Operation(summary = "Delete resource")
    fun deleteResource(@PathVariable resourceId: UUID) =
        resourceService.deleteById(resourceId)

}