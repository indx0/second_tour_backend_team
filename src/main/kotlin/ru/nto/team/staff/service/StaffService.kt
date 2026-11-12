package ru.nto.team.staff.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nto.team.common.RequestValidation
import ru.nto.team.common.exception.ApiException
import ru.nto.team.staff.dto.ProfileDto
import ru.nto.team.staff.repository.StaffRepository

@Service
@Transactional(readOnly = true)
class StaffService(private val staffRepository: StaffRepository) {
    fun profile(code: String): ProfileDto {
        RequestValidation.validateCode(code)

        val prof = staffRepository.findByCode(code) ?: throw ApiException(401, "Unauthorized")

        return ProfileDto(prof.fullName)
    }
}
