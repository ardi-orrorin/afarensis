package com.ardi.afarensis.dto.request

import jakarta.validation.constraints.NotBlank

class RequestPasskey {

    data class Registration(
        @field:NotBlank(message = "json cannot be blank")
        val json: String
    )
}