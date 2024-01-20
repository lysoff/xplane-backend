package app.xplne.api.dto

import java.util.UUID

interface ModelResourceView {
    fun getResourceId(): UUID
    fun getName(): String
    fun getAmount(): Short
}
