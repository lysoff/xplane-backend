package app.xplne.api.exception

import app.xplne.api.dto.ErrorResponseDto
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = mutableListOf<String>()
        val result = ex.bindingResult
        result.fieldErrors.forEach {
            errors.add("${it.objectName}.${it.field}: ${it.defaultMessage}")
        }
        result.globalErrors.forEach {
            errors.add("${it.objectName}: ${it.defaultMessage}")
        }
        val message = "Validation of the '${result.objectName}' object has failed. Errors count: ${result.errorCount}"
        val errorResponse = ErrorResponseDto(HttpStatus.BAD_REQUEST, message, errors)

        return handleExceptionInternal(ex, errorResponse, headers, errorResponse.status, request)
    }
}