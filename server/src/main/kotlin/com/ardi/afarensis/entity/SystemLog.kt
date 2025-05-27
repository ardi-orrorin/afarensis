package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.ErrorLevel
import com.ardi.afarensis.dto.SystemLogDto
import jakarta.persistence.*
import java.time.Instant


@Table(name = "system_logs")
@Entity
class SystemLog(
    @Id
    var id: Long? = 0,

    var message: String,

    var isRead: Boolean,

    @Enumerated(value = EnumType.STRING)
    var level: ErrorLevel,

    var createdAt: Instant = Instant.now()
) {
    fun toDto() = SystemLogDto(
        id = id,
        message = message,
        isRead = isRead,
        level = level,
        createdAt = createdAt
    )
}