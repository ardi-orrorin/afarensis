package com.ardi.afarensis.provider

import com.ardi.afarensis.cache.CacheJavaMailSender
import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.request.RequestSystemSetting
import jakarta.mail.MessagingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
class MailProvider(
    private val javaMailSender: CacheJavaMailSender,
    private val cacheSystemSetting: CacheSystemSetting
) {

    private val log = org.slf4j.LoggerFactory.getLogger(MailProvider::class.java)

    suspend fun testSmtp(req: RequestSystemSetting.Smtp): Boolean = withContext(Dispatchers.IO) {
        val mailSender = JavaMailSenderImpl();
        mailSender.host = req.host
        mailSender.port = req.port.toInt()
        mailSender.username = req.username
        mailSender.password = req.password

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.timeout"] = "5000"
        props["mail.smtp.connectiontimeout"] = "5000"

        try {
            mailSender.testConnection()
            log.trace("SMTP connection success");
            true
        } catch (e: MessagingException) {
            false
        }
    }

    suspend fun sendSystemAlert(subject: String, text: String) {
        cacheSystemSetting.getSystemSetting()[SystemSettingKey.SMTP]?.let {
            sendMail(it.value["host"] as String, subject, text)
        }
    }

    suspend fun sendMail(to: String, subject: String, text: String) = withContext(Dispatchers.IO) {
        val smtp = cacheSystemSetting.getSystemSetting()[SystemSettingKey.SMTP]
            ?: throw IllegalArgumentException("SMTP not configured")

        if (!(smtp.value["enabled"] as Boolean)) return@withContext


        val message = SimpleMailMessage().apply {
            setTo(to)
            setSubject(subject)
            setText(text)
            setFrom(smtp.value["host"] as String)
        }

        javaMailSender.javaMailSender().send(message)

        log.trace("Mail sent to $to")
    }

}