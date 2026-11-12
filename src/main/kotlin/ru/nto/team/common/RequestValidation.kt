package ru.nto.team.common

import ru.nto.team.CreateRequestBody
import ru.nto.team.DecisionBody
import ru.nto.team.common.exception.ApiException
import ru.nto.team.common.exception.RequestValidationException
import ru.nto.team.passrequest.entity.RequestStatus
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

// TODO: normalization, strict date parsing and collecting all available field errors.
object RequestValidation {
    private val codeVerifyRegex = Regex("[A-Za-z0-9]{4}")
    private val dateVerifyRegex = Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}")
    private val phoneVerifyRegex = Regex("\\+7[0-9]{10}")

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        .withResolverStyle(ResolverStyle.STRICT)

    fun validateCode(code: String) {
        if (!codeVerifyRegex.matches(code)) {
            throw ApiException(400, "Invalid code format")
        }
    }

    fun parseStatus(status: String?): RequestStatus? {
        return when (status) {
            null -> null
            "PENDING" -> RequestStatus.PENDING
            "APPROVED" -> RequestStatus.APPROVED
            "REJECTED" -> RequestStatus.REJECTED
            "CANCELLED" -> RequestStatus.CANCELLED
            else -> throw ApiException(400, "Invalid status")
        }
    }

    private fun parseDate(
        value: String?,
        field: String,
        errors: MutableMap<String, String>,
    ): LocalDateTime? {
        val trimmed = value?.trim()

        if (trimmed == null || !dateVerifyRegex.matches(trimmed)) {
            errors[field] =
                "Укажите дату в формате yyyy-MM-dd'T'HH:mm:ss"
            return null
        }

        return try {
            LocalDateTime.parse(trimmed, dateFormatter)
        } catch (_: DateTimeParseException) {
            errors[field] = "Укажите корректную дату и время"
            null
        }
    }

    fun validateCreate(
        body: CreateRequestBody,
        clock: Clock,
    ): CreateRequestBody {
        val errors = linkedMapOf<String, String>()

        val fullName = body.fullName?.trim().orEmpty()
        val phone = body.phone?.trim().orEmpty()
        val organization = body.organization
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
        val purpose = body.purpose?.trim().orEmpty()

        val validFrom = parseDate(body.validFrom, "validFrom", errors)
        val validTo = parseDate(body.validTo, "validTo", errors)

        if (fullName.length !in 5..150) {
            errors["fullName"] = "ФИО должно содержать от 5 до 150 символов"
        }

        if (!phoneVerifyRegex.matches(phone)) {
            errors["phone"] = "Телефон должен содержать +7 и ровно 10 цифр"
        }

        if (organization != null && organization.length > 150) {
            errors["organization"] = "Организация должна содержать не более 150 символов"
        }

        if (purpose.length !in 5..200) {
            errors["purpose"] = "Цель должна содержать от 5 до 200 символов"
        }

        val now = LocalDateTime.ofInstant(clock.instant(), clock.zone)

        if (validFrom != null && validFrom.isBefore(now)) {
            errors["validFrom"] = "Начало не может быть раньше текущего времени"
        }

        if (validFrom != null && validTo != null) {
            if (!validTo.isAfter(validFrom)) {
                errors["validTo"] = "Окончание должно быть позже начала"
            } else if (validTo.toLocalDate() != validFrom.toLocalDate()) {
                errors["validTo"] = "Начало и окончание должны приходиться на один день"
            }
        }

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }

        return CreateRequestBody(
            fullName = fullName,
            phone = phone,
            organization = organization,
            purpose = purpose,
            validFrom = checkNotNull(validFrom).toString(),
            validTo = checkNotNull(validTo).toString(),
        )
    }

    fun validateDecision(body: DecisionBody): DecisionBody {
        val errors = linkedMapOf<String, String>()

        val status = when (body.status?.trim()) {
            "APPROVED" -> RequestStatus.APPROVED
            "REJECTED" -> RequestStatus.REJECTED
            else -> null
        }
        val comment = body.comment?.trim()

        if (status == null) {
            errors["status"] = "Допустимы только APPROVED и REJECTED"
        }

        if (
            status == RequestStatus.REJECTED &&
            (comment == null || comment.length !in 1..500)
        ) {
            errors["comment"] = "Причина отклонения должна содержать от 1 до 500 символов"
        }

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }

        return DecisionBody(
            status = checkNotNull(status).toString(),
            comment = if (status == RequestStatus.REJECTED) comment else null,
        )
    }
}
