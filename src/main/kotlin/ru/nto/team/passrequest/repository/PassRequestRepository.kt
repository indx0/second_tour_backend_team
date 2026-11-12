package ru.nto.team.passrequest.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.nto.team.passrequest.entity.PassRequest
import ru.nto.team.passrequest.entity.RequestStatus
import java.time.LocalDateTime

interface PassRequestRepository: JpaRepository<PassRequest, Long> {
    fun findAllByHostIdOrderByValidFromAscIdAsc(hostId: Long): List<PassRequest>
    fun findAllByHostIdAndStatusOrderByValidFromAscIdAsc(hostId: Long, status: RequestStatus): List<PassRequest>

    fun findByIdAndHostId(id: Long, hostId: Long): PassRequest?

    @Query(
        value = """
        SELECT EXISTS (
            SELECT 1
            FROM pass_request
            WHERE host_id = :hostId
              AND phone = :phone
              AND status IN ('PENDING', 'APPROVED')
              AND valid_from < :validTo
              AND valid_to > :validFrom
        )
    """,
        nativeQuery = true,
    )
    fun existsDuplicate(
        @Param("hostId") hostId: Long,
        @Param("phone") phone: String,
        @Param("validFrom") validFrom: LocalDateTime,
        @Param("validTo") validTo: LocalDateTime,
    ): Boolean
}