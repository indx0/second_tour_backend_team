package ru.nto.team.passrequest.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.nto.team.staff.entity.Staff
import java.time.LocalDateTime

@Entity
@Table(name = "pass_request")
class PassRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_id", nullable = false)
    var host: Staff,

    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String,

    @Column(nullable = false, length = 12)
    var phone: String,

    @Column(length = 150)
    var organization: String?,

    @Column(nullable = false, length = 200)
    var purpose: String,

    @Column(name = "valid_from", nullable = false)
    var validFrom: LocalDateTime,

    @Column(name = "valid_to", nullable = false)
    var validTo: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: RequestStatus,

    @Column(length = 500)
    var comment: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime,
)
