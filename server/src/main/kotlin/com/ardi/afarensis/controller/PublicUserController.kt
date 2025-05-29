package com.ardi.afarensis.controller

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.Role
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.exception.UnauthorizedException
import jakarta.validation.Valid
import kotlinx.coroutines.*
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/public/users")
class PublicUserController(

) : BasicController() {

    @GetMapping("exist-id/{userId}")
    suspend fun existUserId(
        @PathVariable userId: String
    ) = supervisorScope {
        userService.existByUserId(userId)
    }

    @PostMapping("signup")
    suspend fun singUp(
        @Valid @RequestBody req: RequestUser.SignUp
    ) = supervisorScope {
        userService.save(req)
    }


    @PostMapping("signin")
    suspend fun signIn(
        @Valid @RequestBody req: RequestUser.SignIn,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) = supervisorScope {
        val (ip, userAgent) = getIpAndUserAgent(request)

        val signInfo = userService.signIn(req, ip, userAgent)

        listOf(
            ResponseCookieEntity("access_token", signInfo.accessToken, signInfo.accessTokenExpiresIn, false),
            ResponseCookieEntity("refresh_token", signInfo.refreshToken, signInfo.refreshTokenExpiresIn),
            ResponseCookieEntity("user_id", signInfo.userId, signInfo.refreshTokenExpiresIn, false),
            ResponseCookieEntity("roles", signInfo.roles.joinToString(":"), signInfo.accessTokenExpiresIn, false)
        ).map {
            async {
                withContext(Dispatchers.Default) {
                    response.addCookie(createResponseCookie(it))
                }
            }
        }.awaitAll()

        val res = ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "successfully sign in",
            data = true
        )

        ResponseEntity.ok(res)
    }

    @GetMapping("refresh")
    suspend fun publishAccessToken(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ) = supervisorScope {
        val refreshToken = request.cookies["refresh_token"]
            ?.firstOrNull()?.value
            ?: throw UnauthorizedException("refresh token is not found")

        val userId = request.cookies["user_id"]
            ?.firstOrNull()?.value
            ?: throw UnauthorizedException("user id is not found")

        val (ip, userAgent) = getIpAndUserAgent(request)

        val req = RequestUser.RefreshToken(
            refreshToken,
            userId,
            ip,
            userAgent
        )

        val signInfo = userService.publishAccessToken(req)

        listOf(
            ResponseCookieEntity("access_token", signInfo.accessToken, signInfo.accessTokenExpiresIn, false),
            ResponseCookieEntity("roles", signInfo.roles.joinToString(":"), signInfo.accessTokenExpiresIn, false)
        ).map {
            async {
                withContext(Dispatchers.Default) {
                    response.addCookie(
                        createResponseCookie(it)
                    )
                }
            }
        }.awaitAll()

        val res = ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "successfully publish access token",
            data = true
        )

        ResponseEntity.ok(res)
    }

    @PatchMapping("master")
    suspend fun updateMaster(
        @Valid @RequestBody req: RequestUser.InitMasterUpdate
    ) = supervisorScope {
        userService.updateMaster(req, Role.GUEST)
    }

    @DeleteMapping("signout")
    suspend fun signOut(
        @AuthenticationPrincipal principal: UserDetailDto?,
        response: ServerHttpResponse
    ) = supervisorScope {
        removeCookie(response)

        if (principal != null) {
            userService.signOut(principal.userId)
        }

        ResponseEntity.ok(ResponseStatus(status = ResStatus.SUCCESS, message = "successfully sign out", data = true))
    }
}