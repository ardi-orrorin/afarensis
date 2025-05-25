package com.ardi.afarensis.controller

import com.ardi.afarensis.dto.Role
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.service.SystemSettingService
import com.ardi.afarensis.service.UserService
import jakarta.validation.Valid
import kotlinx.coroutines.supervisorScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/public/users")
class PublicUserController(
    private val userService: UserService,
    private val systemSettingService: SystemSettingService,
) {
    @GetMapping("")
    suspend fun getUsers() = supervisorScope {
        userService.findAll()
    }

    @PostMapping("signup")
    suspend fun singUp(
        @Valid @RequestBody req: RequestUser.SignUp
    ) = supervisorScope {
        userService.save(req)
    }

    @PostMapping("signin")
    suspend fun signIn(
        @Valid @RequestBody req: RequestUser.SignIn
    ) = supervisorScope {
        userService.signIn(req)
    }

    @DeleteMapping("signout")
    suspend fun signOut(
        @AuthenticationPrincipal principal: UserDetailDto
    ) = supervisorScope {
        userService.signOut(principal.userId)
    }

    @PostMapping("refresh")
    suspend fun publishAccessToken(
        @Valid @RequestBody req: RequestUser.RefreshToken,
    ) = supervisorScope {
        userService.publishAccessToken(req)
    }

    @PatchMapping("master")
    suspend fun updateMaster(
        @Valid @RequestBody req: RequestUser.InitMasterUpdate
    ) = supervisorScope {
        userService.updateMaster(req, Role.GUEST)
    }

}