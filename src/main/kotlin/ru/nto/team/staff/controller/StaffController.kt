package ru.nto.team.staff.controller

import org.springframework.web.bind.annotation.*
import ru.nto.team.staff.service.StaffService

// TODO: complete auth API behavior; this compilable route delegates to an unfinished service.
@RestController
class StaffController(private val service: StaffService) {
    @GetMapping("/api/{code}/auth")
    fun profile(@PathVariable code: String) = service.profile(code)
}
