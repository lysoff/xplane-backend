package app.xplne.api.controller

import app.xplne.api.dto.ResourceDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/resources")
@Tag(name = "Resources")
class ResourceController {

    @GetMapping
    @Operation(summary = "Find all resources")
    fun findAllResources(@ParameterObject pageable: Pageable): Page<ResourceDto> {
        // TODO implement interaction with DB
        return PageImpl(emptyList())
    }

    @PostMapping
    @Operation(summary = "Create resource")
    fun createResource(@RequestBody dto: ResourceDto): ResourceDto {
        // TODO implement interaction with DB
        return dto
    }

    @GetMapping("/{resourceId}")
    @Operation(summary = "Get resource by ID")
    fun getResourceById(@PathVariable resourceId: UUID): ResourceDto {
        // TODO implement interaction with DB
        return ResourceDto(null, "Resource name")
    }

    @PutMapping("/{resourceId}")
    @Operation(summary = "Update resource")
    fun updateResource(
        @PathVariable resourceId: UUID,
        @RequestBody dto: ResourceDto
    ): ResourceDto {
        // TODO implement interaction with DB
        return dto
    }

    @DeleteMapping("/{resourceId}")
    @Operation(summary = "Delete resource")
    fun deleteResource(@PathVariable resourceId: UUID) {
        // TODO implement interaction with DB
    }
}