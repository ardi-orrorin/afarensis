package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.UserDetailDto
import kotlinx.coroutines.supervisorScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/private/user/users")
class UserUserController : BasicController() {
    @GetMapping("role")
    suspend fun getRole(
        @AuthenticationPrincipal user: UserDetailDto
    ) = supervisorScope {
        user.roles
    }
    
}