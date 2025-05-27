package com.ardi.afarensis.util

import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.ErrorLevel
import com.ardi.afarensis.dto.SystemLogDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.provider.MailProvider
import com.ardi.afarensis.repository.SystemLogRepository
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class RuntimeRegister(
    private val systemSetting: CacheSystemSetting,
    private val mailProvider: MailProvider,
    private val systemLogRepository: SystemLogRepository
) {
    private val log = org.slf4j.LoggerFactory.getLogger(RuntimeRegister::class.java)

    @EventListener(ContextClosedEvent::class)
    fun shutDown() = runBlocking {

        systemSetting.getSystemSetting()[SystemSettingKey.SMTP]?.let {
            if (!(it.value["enabled"] as Boolean)) return@let
            mailProvider.sendSystemAlert("System is shut down", "System is shut down")
        }

        systemSetting.getSystemSetting()[SystemSettingKey.WEBHOOK]?.let {
            if (!(it.value["enabled"] as Boolean)) return@let
        }

        log.trace("System is shut down")

        val log = SystemLogDto(
            message = "System is shut down",
            level = ErrorLevel.SHUTDOWN
        )

        systemLogRepository.save(log.toEntity())
    }
}