package com.ardi.afarensis.dto.request

import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.annotation.ValidPort
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

class RequestSystemSetting {
    data class General(
        val key: SystemSettingKey,
        val value: Map<String, Any>,
        val initValue: Map<String, Any>,
        val public: Boolean,
    )


    data class Smtp(
        @field:NotBlank(message = "Host is required")
        @field:Pattern(
            regexp = "^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*$",
            message = "올바른 호스트명 형식이 아닙니다"
        )
        val host: String,
        @field:NotBlank(message = "Port is required")
        @field:ValidPort
        val port: String,
        @field:NotBlank(message = "Username is required")
        val username: String,
        @field:NotBlank(message = "Password is required")
        val password: String,
        val enabled: Boolean? = false
    )

    data class Init(
        val key: SystemSettingKey
    )
}