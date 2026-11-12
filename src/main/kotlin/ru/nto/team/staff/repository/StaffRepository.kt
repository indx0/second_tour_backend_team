package ru.nto.team.staff.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.nto.team.staff.entity.Staff

interface StaffRepository : JpaRepository<Staff, Long> {
    fun findByCode(code: String): Staff?
}