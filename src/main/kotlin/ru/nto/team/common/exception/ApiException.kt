package ru.nto.team.common.exception

class ApiException(val status: Int, message: String) : RuntimeException(message)