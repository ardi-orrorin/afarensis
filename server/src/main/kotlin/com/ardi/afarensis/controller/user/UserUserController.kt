package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.Role
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.dto.response.ResponseStatus
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/user/users")
class UserUserController : BasicController() {
    @GetMapping("role")
    suspend fun getRole(
        @AuthenticationPrincipal user: UserDetailDto
    ): MutableSet<Role> {
        return user.roles
    }

    @PatchMapping("update-password")
    suspend fun updatePassword(
        @AuthenticationPrincipal user: UserDetailDto,
        @Valid @RequestBody req: RequestUser.UpdatePassword
    ): ResponseStatus<Boolean> {
        return userService.updatePassword(user.id, req)
    }

}