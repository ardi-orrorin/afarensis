package com.ardi.afarensis.cache

import com.ardi.afarensis.dto.SystemSettingKey
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
class CacheJavaMailSender(
    private val cacheSystemSetting: CacheSystemSetting
) {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Cacheable("javaMailSender")
    fun javaMailSender(): JavaMailSender {
        log.trace("Creating mail sender")
        val smtp = cacheSystemSetting.getSystemSetting()[SystemSettingKey.SMTP]?.value
        val enabled = smtp?.get("enabled") as Boolean
        val mailSender = JavaMailSenderImpl();

        val props = mailSender.javaMailProperties

        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"

        if (!enabled) {
            log.warn("SMTP is disabled")
            return mailSender;
        }

        mailSender.host = smtp["host"] as String
        mailSender.port = smtp["port"] as Int
        mailSender.username = smtp["username"] as String
        mailSender.password = smtp["password"] as String

        log.trace("Mail sender created")
        return mailSender
    }

    @CacheEvict("javaMailSender", allEntries = true)
    fun clearCache() {
        log.trace("Clearing mail sender cache")
    }
}
