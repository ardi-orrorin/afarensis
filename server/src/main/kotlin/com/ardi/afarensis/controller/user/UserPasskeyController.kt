package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestPasskey
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.service.PasskeyService
import com.ardi.afarensis.util.CookieUtil
import jakarta.validation.Valid
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/user/passkey")
class UserPasskeyController(
    private val passkeyService: PasskeyService,
    private val cookieUtil: CookieUtil,
) : BasicController() {

    @GetMapping("")
    fun findAllByUserId(
        @AuthenticationPrincipal principal: UserDetailDto
    ) = passkeyService.findAllByUserPk(principal.id)

    @GetMapping("credential")
    fun getCredential(
        @AuthenticationPrincipal principal: UserDetailDto,
    ) = passkeyService.createCredentialOptions(principal.userId)


    @PostMapping("registration")
    fun registration(
        @AuthenticationPrincipal principal: UserDetailDto,
        @Valid @RequestBody req: RequestPasskey.Registration,
        request: ServerHttpRequest,
    ): ResponseStatus<Boolean> {
        val (ip, userAgent) = cookieUtil.getIpAndUserAgent(request)

        return passkeyService.finishRegistration(principal.userId, req.json, userAgent)
    }

    @DeleteMapping("")
    fun delete(
        @RequestParam id: String,
        @AuthenticationPrincipal principal: UserDetailDto
    ) = passkeyService.delete(principal.id, id)

}