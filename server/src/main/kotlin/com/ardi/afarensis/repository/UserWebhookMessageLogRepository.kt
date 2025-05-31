package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.User
import com.ardi.afarensis.entity.UserWebhookMessageLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface UserWebhookMessageLogRepository : JpaRepository<UserWebhookMessageLog, Long> {
    fun findAllByUser(user: User, pageable: Pageable): Page<UserWebhookMessageLog>
}