package com.ardi.afarensis.controller

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.Role
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.UserDto
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.exception.UnauthorizedException
import com.ardi.afarensis.service.SystemSettingService
import jakarta.validation.Valid
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/v1/public/users")
class PublicUserController(
    private val systemSettingService: SystemSettingService,
) : BasicController() {

    @GetMapping("exist-id/{userId}")
    suspend fun existUserId(
        @PathVariable userId: String
    ) = userService.existByUserId(userId)
    

    @PostMapping("signup")
    suspend fun singUp(
        @Valid @RequestBody req: RequestUser.SignUp
    ) = userService.save(req)


    @PostMapping("signin")
    suspend fun signIn(
        @Valid @RequestBody req: RequestUser.SignIn,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): ResponseEntity<ResponseStatus<Boolean>> = supervisorScope {
        val (ip, userAgent) = getIpAndUserAgent(request)

        val signInfo = userService.signIn(req, ip, userAgent);

        val base64Roles = Base64.getEncoder()
            .encode(signInfo.roles.joinToString(":").encodeToByteArray())
            .toString(Charsets.UTF_8)

        val userId = Base64.getEncoder()
            .encode(signInfo.userId.encodeToByteArray())
            .toString(Charsets.UTF_8)

        listOf(
            ResponseCookieEntity(
                "access_token", signInfo.accessToken, signInfo.accessTokenExpiresIn, false
            ),
            ResponseCookieEntity(
                "refresh_token", signInfo.refreshToken, signInfo.refreshTokenExpiresIn
            ),
            ResponseCookieEntity(
                "user_id", userId, signInfo.refreshTokenExpiresIn, false
            ),
            ResponseCookieEntity(
                "roles",
                base64Roles,
                signInfo.refreshTokenExpiresIn,
                false
            )
        ).map {
            async {
                mutex.withLock {
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

    @PostMapping("reset-password")
    suspend fun resetPassword(
        @Valid @RequestBody req: RequestUser.ResetPassword
    ) = userService.resetPassword(req)


    @GetMapping("refresh")
    suspend fun publishAccessToken(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ) = supervisorScope {
        val refreshToken = request.cookies["refresh_token"]
            ?.firstOrNull()?.value
            ?: throw UnauthorizedException("refresh token is not found")

        val base64UserId = request.cookies["user_id"]
            ?.firstOrNull()?.value
            ?: throw UnauthorizedException("user id is not found")

        val decodedUserId = Base64.getDecoder()
            .decode(base64UserId)
            .toString(Charsets.UTF_8)


        val (ip, userAgent) = getIpAndUserAgent(request)

        val req = RequestUser.RefreshToken(
            refreshToken,
            decodedUserId,
            ip,
            userAgent
        )

        val signInfo = userService.publishAccessToken(req)

        val base64Roles =
            Base64.getEncoder()
                .encode(signInfo.roles.joinToString(":").encodeToByteArray())
                .toString(Charsets.UTF_8)

        listOf(
            ResponseCookieEntity("access_token", signInfo.accessToken, signInfo.accessTokenExpiresIn, false),
            ResponseCookieEntity(
                "user_id", base64UserId, signInfo.refreshTokenExpiresIn, false
            ),
            ResponseCookieEntity(
                "roles",
                base64Roles,
                signInfo.refreshTokenExpiresIn,
                false
            )
        ).map {
            async {
                mutex.withLock {
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
    ): ResponseStatus<Boolean> {
        val res = userService.updateMaster(req, Role.GUEST)

        if (res.data!!) {
            withContext(Dispatchers.IO) {
                systemSettingService.updateInit(req.homeUrl)
            }
        }

        return res;
    }

    @DeleteMapping("signout")
    suspend fun signOut(
        @AuthenticationPrincipal principal: UserDetailDto?,
        response: ServerHttpResponse
    ): ResponseEntity<ResponseStatus<Boolean>> {
        removeCookie(response)

        if (principal != null) {
            userService.signOut(principal.userId)
        }

        return ResponseEntity.ok(
            ResponseStatus(
                status = ResStatus.SUCCESS,
                message = "successfully sign out",
                data = true
            )
        )
    }
}