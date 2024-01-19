package app.xplne.api.controller

import app.xplne.api.constants.BASE_PATH_ACTIVITIES
import app.xplne.api.constants.ENTITY_NOT_FOUND
import app.xplne.api.constants.ENTITY_UPDATED
import app.xplne.api.constants.PATH_ACTIVITY_ID
import app.xplne.api.dto.ActivityDto
import app.xplne.api.service.ActivityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
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
    fun createActivity(
        @RequestBody dto: ActivityDto
    ): ActivityDto =
        activityService.create(dto)

    @PutMapping(PATH_ACTIVITY_ID)
    @Operation(summary = "Update activity")
    @ApiResponse(responseCode = "200", description = ENTITY_UPDATED)
    @ApiResponse(responseCode = "404", description = ENTITY_NOT_FOUND, content = [Content()])
    fun updateActivity(
        @PathVariable activityId: UUID,
        @RequestBody dto: ActivityDto
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