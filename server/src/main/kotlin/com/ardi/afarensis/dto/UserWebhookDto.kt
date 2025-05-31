package com.ardi.afarensis.dto

import java.time.Instant

data class UserWebhookDto(
    val id: Long? = null,
    val userPk: String,
    val type: WebhookType,
    val url: String,
    val secret: String,
    val createdAt: Instant,
    val messageLogs: MutableList<UserWebhookMessageLogDto> = mutableListOf()
) {
}