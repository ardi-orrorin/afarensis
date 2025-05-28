package com.ardi.afarensis.util

import ch.qos.logback.classic.LoggerContext
import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.ErrorLevel
import com.ardi.afarensis.dto.SystemLogDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.provider.MailProvider
import com.ardi.afarensis.repository.SystemLogRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicBoolean


@Component
class RuntimeRegister(
    private val systemSetting: CacheSystemSetting,
    private val mailProvider: MailProvider,
    private val systemLogRepository: SystemLogRepository
) {
    private val log = org.slf4j.LoggerFactory.getLogger(RuntimeRegister::class.java)

    private val shutdownExecuted = AtomicBoolean(false)

    @EventListener(ContextClosedEvent::class)
    @Transactional
    fun shutDown(event: ContextClosedEvent) = runBlocking {
        if (!shutdownExecuted.compareAndSet(false, true)) {
            log.debug("Shutdown already executed, skipping...")
            return@runBlocking
        }

        if (event.applicationContext.parent != null) {
            log.debug("Child context closed, skipping...")
            return@runBlocking
        }

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

    @PostConstruct
    fun registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                log.error("EMERGENCY: Application shutting down - potential SIGKILL")
                logCriticalSystemState()
                (LoggerFactory.getILoggerFactory() as LoggerContext).stop()
            } catch (e: Exception) {
                System.err.println("Emergency log failed: ${e.message}")
            }
        })
    }

    private fun logCriticalSystemState() {
        val runtime = Runtime.getRuntime()
        log.error(
            "Memory: {}MB used / {}MB total",
            (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024,
            runtime.totalMemory() / 1024 / 1024
        )
        log.error("Active threads: {}", Thread.activeCount())
    }
}