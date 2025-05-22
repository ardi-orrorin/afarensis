package com.ardi.afarensis.controller.admin

import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.service.UserService
import kotlinx.coroutines.supervisorScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/private/admin/users")
class AdminUserController(
    private val userService: UserService
) {
    @GetMapping("")
    suspend fun getUsers() = supervisorScope {
        userService.findAll()
    }

    @GetMapping("/me")
    suspend fun getMyUser(
        @AuthenticationPrincipal principal: UserDetailDto
    ) = supervisorScope { userService.findByUserId(principal.userId) }
}