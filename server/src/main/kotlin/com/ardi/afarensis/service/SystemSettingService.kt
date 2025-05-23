package com.ardi.afarensis.service

import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.repository.SystemSettingRepository
import kotlinx.coroutines.supervisorScope
import org.springframework.cloud.context.scope.refresh.RefreshScope
import org.springframework.stereotype.Service

@Service
class SystemSettingService(
    private val systemSettingRepository: SystemSettingRepository,
    private val systemSettings: Map<SystemSettingKey, SystemSettingDto>,
    private val refreshScope: RefreshScope,
) {

    suspend fun findByKey(key: SystemSettingKey) = supervisorScope {
        systemSettingRepository.findByKey(key)
    }

    suspend fun findAllByPublic(isPublic: Boolean) = supervisorScope {
        systemSettings.values.filter { it.public == isPublic }
    }

    suspend fun update(key: SystemSettingKey, value: Map<String, Any>) = supervisorScope {
        val systemSetting = systemSettingRepository.findByKey(key)
            ?: throw IllegalArgumentException("System setting not found")

        systemSetting.value = value

        systemSettingRepository.save(systemSetting)

        refreshScope.refresh("systemSetting")
    }
}