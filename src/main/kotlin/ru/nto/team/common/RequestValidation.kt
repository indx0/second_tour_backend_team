package ru.nto.team.common

import ru.nto.team.common.exception.ApiException

// TODO: normalization, strict date parsing and collecting all available field errors.
object RequestValidation {
    private val codeVerifyRegex = Regex("[A-Za-z0-9]{4}")

    fun validateCode(code: String) {
        if (!codeVerifyRegex.matches(code)) {
            throw ApiException(400, "Invalid code format")
        }
    }


}
