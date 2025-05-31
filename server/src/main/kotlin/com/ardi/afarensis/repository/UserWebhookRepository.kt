package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.UserWebhook
import org.springframework.data.jpa.repository.JpaRepository

interface UserWebhookRepository : JpaRepository<UserWebhook, Long> {
}