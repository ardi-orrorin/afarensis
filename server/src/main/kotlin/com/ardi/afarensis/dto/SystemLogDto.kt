package com.ardi.afarensis.dto

import com.ardi.afarensis.entity.SystemLog
import java.time.Instant

data class SystemLogDto(
    val id: Long? = null,
    val message: String,
    val isRead: Boolean = false,
    var level: ErrorLevel,
    val createdAt: Instant = Instant.now()
) {
    fun toEntity() = SystemLog(
        null,
        message,
        isRead,
        level,
        createdAt
    )
}