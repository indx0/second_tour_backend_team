package ru.nto.team.passrequest.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nto.team.CreateRequestBody
import ru.nto.team.DecisionBody
import ru.nto.team.common.RequestValidation
import ru.nto.team.common.exception.ApiException
import ru.nto.team.passrequest.dto.RequestDetailsDto
import ru.nto.team.passrequest.dto.RequestListDto
import ru.nto.team.passrequest.entity.PassRequest
import ru.nto.team.passrequest.entity.RequestStatus
import ru.nto.team.passrequest.repository.PassRequestRepository
import ru.nto.team.staff.service.StaffService
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
@Transactional(readOnly = true)
class RequestService(
    private val repo: PassRequestRepository,
    private val staffService: StaffService,
    private val clock: Clock,
) {
    fun list(code: String, status: String?): List<RequestListDto> {
        val staff = staffService.getStaff(code);
        val status = RequestValidation.parseStatus(status)

        val requests = if (status == null) repo.findAllByHostIdOrderByValidFromAscIdAsc(staff.id!!) else repo.findAllByHostIdAndStatusOrderByValidFromAscIdAsc(staff.id!!, status);

        return requests.map {
            RequestListDto(
                it.id!!,
                it.fullName,
                it.validFrom,
                it.validTo,
                it.status,
            )
        }
    }

    fun details(code: String, id: Long): RequestDetailsDto {
        val staff = staffService.getStaff(code)
        val reqeust = repo.findByIdAndHostId(id, staff.id!!) ?: throw ApiException(404, "Request not found")

        return reqeust.toDetails();
    }

    @Transactional(readOnly = false)
    fun create(
        code: String,
        body: CreateRequestBody,
    ): RequestDetailsDto {
        val staff = staffService.getStaff(code);
        val data = RequestValidation.validateCreate(body, clock)

        val duplicate = repo.existsDuplicate(
            hostId = staff.id!!,
            phone = data.phone!!,
            validFrom = LocalDateTime.parse(data.validFrom!!),
            validTo = LocalDateTime.parse(data.validTo!!),
        )

        if (duplicate) {
            throw ApiException(409, "Duplicate request")
        }

        val now = now()

        return repo.save(
            PassRequest(
                host = staff,
                fullName = data.fullName!!,
                phone = data.phone,
                organization = data.organization,
                purpose = data.purpose!!,
                validFrom = LocalDateTime.parse(data.validFrom),
                validTo = LocalDateTime.parse(data.validTo),
                status = RequestStatus.PENDING,
                comment = null,
                createdAt = now,
                updatedAt = now,
            )
        ).toDetails()
    }
    @Transactional(readOnly = false)
    fun cancel(code: String, id: Long): RequestDetailsDto {
        val staff = staffService.getStaff(code)
        val reqeust = repo.findByIdAndHostId(id, staff.id!!) ?: throw ApiException(404, "Request not found")

        if (reqeust.status != RequestStatus.PENDING) {
            throw ApiException(409, "Invalid status transition")
        }

        reqeust.status = RequestStatus.CANCELLED
        reqeust.updatedAt = now()

        return reqeust.toDetails()
    }

    @Transactional(readOnly = false)
    fun decide(id: Long, body: DecisionBody): RequestDetailsDto {
        val request = repo.findById(id).orElseThrow {
            ApiException(404, "Request not found")
        }

        val decision = RequestValidation.validateDecision(body)
        if (request.status != RequestStatus.PENDING) {
            throw ApiException(409, "Invalid status transition")
        }

        request.status = RequestValidation.parseStatus(decision.status)!!
        request.comment = decision.comment
        request.updatedAt = now()

        return request.toDetails()
    }


    private fun PassRequest.toDetails() = RequestDetailsDto(
        id = id!!,
        fullName = fullName,
        phone = phone,
        organization = organization,
        purpose = purpose,
        validFrom = validFrom,
        validTo = validTo,
        status = status,
        comment = comment,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun now(): LocalDateTime {
        return LocalDateTime.ofInstant(
            clock.instant(),
            clock.zone,
        ).truncatedTo(ChronoUnit.SECONDS)
    }

}
