package app.xplne.api.dto

import java.util.*

interface ActivityImpactView {
    fun getResourceId(): UUID
    fun getQuantity(): Short
}
