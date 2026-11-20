package ru.nto.team

import org.springframework.stereotype.Service
import ru.nto.team.common.exception.ApiException
import ru.nto.team.passrequest.dto.RequestDetailsDto
import ru.nto.team.passrequest.dto.RequestListDto


@Service
class RequestService {
    fun list(code: String, status: String?): List<RequestListDto> = throw ApiException(501, "Task not implemented")
    fun details(code: String, id: Long): RequestDetailsDto = throw ApiException(501, "Task not implemented")
    fun create(code: String, body: CreateRequestBody): RequestDetailsDto = throw ApiException(501, "Task not implemented")
    fun cancel(code: String, id: Long): RequestDetailsDto = throw ApiException(501, "Task not implemented")
    fun decide(id: Long, body: DecisionBody): RequestDetailsDto = throw ApiException(501, "Task not implemented")
}