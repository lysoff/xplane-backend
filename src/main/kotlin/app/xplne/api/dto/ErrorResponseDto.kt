package app.xplne.api.dto

import org.springframework.http.HttpStatus


data class ErrorResponseDto(
    val status: HttpStatus,
    val message: String,
    var errors: MutableList<String>? = null
)
