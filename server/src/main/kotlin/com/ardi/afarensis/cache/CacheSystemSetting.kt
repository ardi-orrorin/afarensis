package com.ardi.afarensis.cache

import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.repository.SystemSettingRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component


@Component
class CacheSystemSetting(
    private val systemSettingRepository: SystemSettingRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Cacheable("systemSetting")
    fun getSystemSetting(): Map<SystemSettingKey, SystemSettingDto> {
        log.trace("System Setting Loaded from database")
        return systemSettingRepository.findAll()
            .associate { it.key to it.toDto() }
    }

    @CacheEvict("systemSetting", allEntries = true)
    fun clearCache() {
        log.trace("System Setting Cache Cleared")
    }
}