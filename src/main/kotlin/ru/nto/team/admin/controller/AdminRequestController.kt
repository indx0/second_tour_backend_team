package ru.nto.team.admin.controller

import org.springframework.web.bind.annotation.*
import ru.nto.team.DecisionBody
import ru.nto.team.passrequest.service.RequestService

// TODO: decision behavior. The admin-key check is already protected infrastructure.
@RestController
class AdminRequestController(private val service: RequestService) {
    @PatchMapping("/api/admin/requests/{id}")
    fun decide(@PathVariable id: Long, @RequestBody body: DecisionBody) = service.decide(id, body)
}
