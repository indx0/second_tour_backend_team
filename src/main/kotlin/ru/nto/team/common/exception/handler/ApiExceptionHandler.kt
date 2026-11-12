package ru.nto.team.common.exception.handler

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.nto.team.common.exception.ApiException
import ru.nto.team.common.exception.RequestValidationException

// TODO: implement domain errors and validation errors. This only makes unfinished routes usable.
@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(ApiException::class)
    fun unfinished(error: ApiException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(error.status).body(mapOf("error" to error.message!!))

    @ExceptionHandler(RequestValidationException::class)
    fun requestValidationExceptionHandler(error: RequestValidationException): ResponseEntity<Map<String, Map<String, String>>> =
        ResponseEntity.badRequest().body(mapOf("errors" to error.errors))
}
