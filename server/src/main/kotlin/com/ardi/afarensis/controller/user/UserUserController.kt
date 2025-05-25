package com.ardi.afarensis.controller.user

import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.service.UserService
import kotlinx.coroutines.supervisorScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/private/user/users")
class UserUserController(
    private val userService: UserService,
) {
    @GetMapping("role")
    suspend fun getRole(
        @AuthenticationPrincipal user: UserDetailDto
    ) = supervisorScope {
        user.roles
    }


}