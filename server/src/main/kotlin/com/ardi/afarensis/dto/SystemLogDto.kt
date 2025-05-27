package com.ardi.afarensis.dto

import com.ardi.afarensis.entity.SystemLog
import java.time.Instant

data class SystemLogDto(
    val id: Long? = 0,
    val message: String,
    val isRead: Boolean = false,
    var level: ErrorLevel,
    val createdAt: Instant = Instant.now()
) {
    fun toEntity() = SystemLog(
        id = id,
        message = message,
        isRead = isRead,
        level = level,
        createdAt = createdAt
    )
}