package com.ardi.afarensis.dto

import java.time.Instant

data class UserWebhookMessageLogDto(
    val id: Long? = null,
    val userPk: String,
    val webhookPk: Long,
    val message: Map<String, Any>,
    val createdAt: Instant = Instant.now()
)
