package com.ardi.afarensis.service

import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

open class BasicService {

    @Autowired
    lateinit var systemSetting: CacheSystemSetting

    @Autowired
    lateinit var userRepository: UserRepository

    fun getCacheSystemSettingKey(key: SystemSettingKey): SystemSettingDto? {
        return systemSetting.getSystemSetting()[key]
    }

    fun getCacheSystemSetting(): Map<SystemSettingKey, SystemSettingDto> {
        return systemSetting.getSystemSetting()
    }
}