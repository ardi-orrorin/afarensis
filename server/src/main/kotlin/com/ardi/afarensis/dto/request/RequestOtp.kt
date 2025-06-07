package com.ardi.afarensis.dto.request

import jakarta.validation.constraints.NotBlank

class RequestOtp {

    data class VerifyCode(
        @field:NotBlank(message = "code cannot be blank")
        val code: String
    )
}