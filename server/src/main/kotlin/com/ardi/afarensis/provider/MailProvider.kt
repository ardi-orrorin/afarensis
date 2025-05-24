package com.ardi.afarensis.provider

import com.ardi.afarensis.dto.request.RequestSystemSetting
import jakarta.mail.MessagingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
class MailProvider(
    private val javaMailSender: JavaMailSender
) {

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
            true
        } catch (e: MessagingException) {
            false
        }
    }

}