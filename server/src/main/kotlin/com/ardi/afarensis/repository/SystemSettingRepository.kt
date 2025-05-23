package com.ardi.afarensis.repository

import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.entity.SystemSetting
import org.springframework.data.jpa.repository.JpaRepository

interface SystemSettingRepository : JpaRepository<SystemSetting, Long> {
    fun findByKey(key: SystemSettingKey): SystemSetting?
    fun findAllByPublic(isPublic: Boolean): MutableList<SystemSetting>
}