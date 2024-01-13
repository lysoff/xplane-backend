package app.xplne.api.controller

import app.xplne.api.dto.ActivityDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/activities")
@Tag(name = "Activities")
class ActivityController() {

    @GetMapping
    @Operation(summary = "Find all activities")
    fun findAllActivities(@ParameterObject pageable: Pageable): Slice<ActivityDto> {
        // TODO implement interaction with DB
        return SliceImpl(emptyList())
    }

    @PostMapping
    @Operation(summary = "Create activity")
    fun createActivity(@RequestBody dto: ActivityDto): ActivityDto {
        // TODO implement interaction with DB
        return dto
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "Get activity by ID")
    fun getActivityById(@PathVariable activityId: UUID): ActivityDto {
        // TODO implement interaction with DB
        return ActivityDto(null, "name")
    }

    @PutMapping("/{activityId}")
    @Operation(summary = "Update activity")
    fun updateActivity(
        @PathVariable activityId: UUID,
        @RequestBody dto: ActivityDto
    ): ActivityDto {
        // TODO implement interaction with DB
        return dto
    }

    @DeleteMapping("/{activityId}")
    @Operation(summary = "Delete activity")
    fun deleteActivity(@PathVariable activityId: UUID) {
        // TODO implement interaction with DB
    }

}