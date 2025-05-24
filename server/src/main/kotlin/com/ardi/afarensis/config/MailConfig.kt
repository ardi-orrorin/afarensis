package com.ardi.afarensis.config

import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
@Slf4j
class MailConfig {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    @RefreshScope
    fun javaMailSender(systemSettings: Map<SystemSettingKey, SystemSettingDto>): JavaMailSender {
        log.trace("Creating mail sender")
        val smtp = systemSettings[SystemSettingKey.SMTP]?.value
        val enabled = smtp?.get("enabled") as Boolean
        val mailSender = JavaMailSenderImpl();


        if (!enabled) {
            log.warn("SMTP is disabled")
            return mailSender;
        }

        mailSender.host = smtp["host"] as String
        mailSender.port = smtp["port"] as Int
        mailSender.username = smtp["username"] as String
        mailSender.password = smtp["password"] as String

        val props = mailSender.javaMailProperties

        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"

        log.trace("Mail sender created")
        return mailSender
    }
}