package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.SystemLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface SystemLogRepository : JpaRepository<SystemLog, Long> {

    @Modifying
    @Transactional
    @Query("update SystemLog set isRead = true where id = :id")
    fun updateIsReadById(id: Long): Int
}