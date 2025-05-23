package com.ardi.afarensis.config

import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.repository.SystemSettingRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@Slf4j
class SystemSettingConfig {

    @Bean
    @RefreshScope
    fun systemSetting(systemSettingRepository: SystemSettingRepository): Map<SystemSettingKey, SystemSettingDto> {
        println("System Setting Loaded")
        return systemSettingRepository.findAll()
            .associate { it.key to it.toDto() }
    }
}