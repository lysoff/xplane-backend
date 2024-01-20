package app.xplne.api.controller

import app.xplne.api.constants.*
import app.xplne.api.dto.ActivityDto
import app.xplne.api.dto.ErrorResponseDto
import app.xplne.api.dto.scope.Basic
import app.xplne.api.dto.scope.OnCreate
import app.xplne.api.service.ActivityService
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
@RequestMapping(BASE_PATH_ACTIVITIES)
@Tag(name = "Activities")
class ActivityController(
    private val activityService: ActivityService
) {
    @GetMapping
    @Operation(summary = "Find all activities")
    fun findAllActivities(@ParameterObject pageable: Pageable): Slice<ActivityDto> =
        activityService.findAll(pageable)

    @PostMapping
    @Operation(summary = "Create activity")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = ENTITY_UPDATED),
        ApiResponse(responseCode = "400", description = DTO_VALIDATION_FAILED,
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]),
        ApiResponse(responseCode = "404", description = ENTITY_NOT_FOUND, content = [Content()]),
    )
    fun createActivity(
        @RequestBody @Validated(OnCreate::class) dto: ActivityDto
    ): ActivityDto =
        activityService.create(dto)

    @PutMapping(PATH_ACTIVITY_ID)
    @Operation(summary = "Update activity")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = ENTITY_UPDATED),
        ApiResponse(responseCode = "400", description = DTO_VALIDATION_FAILED,
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]),
        ApiResponse(responseCode = "404", description = ENTITY_NOT_FOUND, content = [Content()]),
    )
    fun updateActivity(
        @PathVariable activityId: UUID,
        @RequestBody @Validated(Basic::class) dto: ActivityDto
    ): ActivityDto {
        dto.id = activityId
        return activityService.update(dto)
    }

    @GetMapping(PATH_ACTIVITY_ID)
    @Operation(summary = "Get activity by ID")
    fun getActivityById(@PathVariable activityId: UUID): ActivityDto? =
        activityService.findByIdOrNull(activityId)

    @DeleteMapping(PATH_ACTIVITY_ID)
    @Operation(summary = "Delete activity")
    fun deleteActivity(@PathVariable activityId: UUID) =
        activityService.deleteById(activityId)

}