package ru.nto.team.common.exception

class RequestValidationException(
    val errors: Map<String, String>,
) : RuntimeException("Request validation failed")
