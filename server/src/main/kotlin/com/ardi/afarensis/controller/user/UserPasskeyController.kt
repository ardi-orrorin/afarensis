package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestPasskey
import com.ardi.afarensis.service.PasskeyService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/user/passkey")
class UserPasskeyController(
    private val passkeyService: PasskeyService
) : BasicController() {

    @GetMapping("")
    fun findAllByUserId(
        @AuthenticationPrincipal principal: UserDetailDto
    ) = passkeyService.findAllByUserPk(principal.id)

    @GetMapping("credential")
    fun getCredential(
        @AuthenticationPrincipal principal: UserDetailDto,
    ) = passkeyService.createCredentialOptions(principal.id)


    @PostMapping("registration")
    fun registration(
        @AuthenticationPrincipal principal: UserDetailDto,
        @Valid @RequestBody req: RequestPasskey.Registration
    ) = passkeyService.finishRegistration(principal.id, req.json)

    @DeleteMapping("")
    fun delete(
        @RequestParam id: String,
        @AuthenticationPrincipal principal: UserDetailDto
    ) = passkeyService.delete(principal.id, id)

}