package com.ardi.afarensis.provider

import com.ardi.afarensis.cache.CacheJavaMailSender
import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.request.RequestSystemSetting
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.internet.InternetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        mailSender.defaultEncoding = "UTF-8"
        mailSender.protocol = "smtp"


        mailSender.javaMailProperties["mail.smtp.auth"] = "true"
        mailSender.javaMailProperties["mail.smtp.starttls.enable"] = "true"
        mailSender.javaMailProperties["mail.debug"] = "true"
        mailSender.javaMailProperties["mail.smtp.ssl.trust"] = "*"
        mailSender.javaMailProperties["mail.smtp.connectiontimeout"] = "5000"
        mailSender.javaMailProperties["mail.smtp.timeout"] = "5000"
        mailSender.javaMailProperties["mail.smtp.writetimeout"] = "5000"


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

        val username = smtp.value["username"] as String
        val host = smtp.value["host"] as String

        val domain = host.split(".").takeLast(2).joinToString(".")

        val from = "$username@$domain"

        if (!(smtp.value["enabled"] as Boolean)) return@withContext

        val message = javaMailSender.javaMailSender().createMimeMessage()
        message.setSubject(subject)
        message.setText(text)
        message.setFrom(from)
        message.addRecipient(
            Message.RecipientType.TO, InternetAddress(to)
        )

        javaMailSender.javaMailSender().send(message)

        log.trace("Mail sent to $to")
    }

}