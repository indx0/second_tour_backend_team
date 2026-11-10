package ru.nto.team.staff.service

import org.springframework.stereotype.Service
import ru.nto.team.common.ApiException
import ru.nto.team.ProfileDto

@Service
class StaffService {
    fun profile(code: String): ProfileDto = throw ApiException(501, "Task not implemented")
}
