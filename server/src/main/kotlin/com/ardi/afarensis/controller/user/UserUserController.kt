package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestUser
import jakarta.validation.Valid
import kotlinx.coroutines.supervisorScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/user/users")
class UserUserController : BasicController() {
    @GetMapping("role")
    suspend fun getRole(
        @AuthenticationPrincipal user: UserDetailDto
    ) = supervisorScope {
        user.roles
    }

    @PatchMapping("update-password")
    suspend fun updatePassword(
        @AuthenticationPrincipal user: UserDetailDto,
        @Valid @RequestBody req: RequestUser.UpdatePassword
    ) = supervisorScope {
        userService.updatePassword(user.id, req)
    }

}