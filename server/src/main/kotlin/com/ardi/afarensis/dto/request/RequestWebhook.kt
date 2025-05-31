package com.ardi.afarensis.dto.request

import com.ardi.afarensis.dto.WebhookType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class RequestWebhook {
    data class SaveWebhook(
        val id: Long? = null,
        @field:NotNull(message = "Type is required")
        val type: WebhookType,
        @field:NotBlank(message = "Type is required")
        val url: String,
        val secret: String = ""
    )
}