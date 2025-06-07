package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestOtp
import com.ardi.afarensis.service.OtpService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/user/otp")
class UserOtpController(
    private val otpService: OtpService,
) : BasicController() {

    @GetMapping("/qrcode")
    fun getQrCode(
        @AuthenticationPrincipal principal: UserDetailDto,
    ) = otpService.generateOtp(principal.id)

    @PostMapping("/verify")
    fun verifyOtp(
        @AuthenticationPrincipal principal: UserDetailDto,
        @Valid @RequestBody req: RequestOtp.VerifyCode,
    ) = otpService.verifyOtp(principal.id, req.code)
}