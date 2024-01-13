package app.xplne.api.dto

import java.util.UUID

interface ModelActivityView {
    fun getActivityId(): UUID
    fun getName(): String
    fun getImpacts(): List<ActivityImpactView>
}
