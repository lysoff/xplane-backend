package app.xplne.api.dto

import java.util.*

interface ModelFullView {
    fun getId(): UUID
    fun getName(): String
    fun getResources(): List<ModelResourceView>
    fun getActivities(): List<ModelActivityView>
}
