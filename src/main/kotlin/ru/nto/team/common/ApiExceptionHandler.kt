package ru.nto.team.common

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// TODO: implement domain errors and validation errors. This only makes unfinished routes usable.
class ApiException(val status: Int, message: String) : RuntimeException(message)

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(ApiException::class)
    fun unfinished(error: ApiException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(error.status).body(mapOf("error" to error.message!!))
}
