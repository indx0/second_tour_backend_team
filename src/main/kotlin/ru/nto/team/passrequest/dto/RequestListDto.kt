package ru.nto.team.passrequest.dto

import ru.nto.team.passrequest.entity.RequestStatus
import java.time.LocalDateTime

data class RequestListDto(
    val id: Long,
    val fullName: String,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val status: RequestStatus,
)
