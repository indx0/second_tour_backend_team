package ru.nto.team.passrequest.entity

import ru.nto.team.staff.entity.Staff
import java.time.LocalDateTime

// TODO: JPA entity for pass_request, including a staff association and generated Long ID.
// No @Entity yet: the unfinished model must not prevent application startup.
class PassRequest(
    var id: Long = 0,
    var host: Staff,
    var fullName: String,
    var phone: String,
    var organization: String?,
    var purpose: String,
    var validFrom: LocalDateTime,
    var validTo: LocalDateTime,
    var status: RequestStatus,
    var comment: String?,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
)
