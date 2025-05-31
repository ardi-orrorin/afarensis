package com.ardi.afarensis.dto

import com.ardi.afarensis.entity.UserRefreshToken
import java.time.Instant

data class UserDto(
    val id: String,
    val pwd: String,
    val userId: String,
    val email: String,
    val roles: MutableSet<Role> = mutableSetOf(),
    val profileImg: String,
    val createdAt: Instant,
    val userRefreshToken: UserRefreshToken? = null,
    val webhooks: MutableSet<UserWebhookDto> = mutableSetOf(),
    val webhookMessageLogs: MutableList<UserWebhookMessageLogDto> = mutableListOf(),
)
