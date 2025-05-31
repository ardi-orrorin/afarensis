package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.UserWebhookMessageLogDto
import com.ardi.afarensis.dto.request.RequestWebhook
import com.ardi.afarensis.dto.response.PageResponse
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.dto.response.ResponseWebhook
import com.ardi.afarensis.entity.UserWebhook
import com.ardi.afarensis.repository.UserWebhookMessageLogRepository
import com.ardi.afarensis.repository.UserWebhookRepository
import com.ardi.afarensis.util.toResponse
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WebhookService(
    private val webhookRepository: UserWebhookRepository,
    private val userWebhookMessageLogRepository: UserWebhookMessageLogRepository,
) : BasicService() {

    @Transactional(readOnly = true)
    fun findAllByUserId(userPK: String): ResponseWebhook.List {
        val user = userRepository.findById(userPK)
            .orElseThrow { IllegalArgumentException("User not found") }

        val webhooks = user.webhooks.map { it.toDto() }.toMutableList()

        return ResponseWebhook.List(
            data = webhooks
        )
    }

    @Transactional(readOnly = true)
    fun findMessageLogByUserPk(
        userPk: String,
        toPageRequest: PageRequest
    ): PageResponse<UserWebhookMessageLogDto> {
        val user = userRepository.findById(userPk)
            .orElseThrow { IllegalArgumentException("User not found") }

        val logs = userWebhookMessageLogRepository.findAllByUser(user, toPageRequest)
        return logs.toResponse(logs.content.map { it.toDto() })
    }


    fun saveWebhook(userPk: String, req: RequestWebhook.SaveWebhook): ResponseStatus<Boolean> {
        val user = userRepository.findById(userPk)
            .orElseThrow { IllegalArgumentException("User not found") }

        // TODO: Test Logic

        user.let {
            val webhook = UserWebhook(
                url = req.url,
                type = req.type,
                secret = req.secret
            )

            it.addWebhook(webhook)

            webhookRepository.save(webhook)
        }

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Webhook saved",
            data = true
        )
    }

    fun updateWebhook(userPk: String, req: RequestWebhook.SaveWebhook): ResponseStatus<Boolean> {
        val user = userRepository.findById(userPk)
            .orElseThrow { IllegalArgumentException("User not found") }

        user.webhooks.find { it.id == req.id }?.let {
            it.url = req.url
            it.type = req.type
            it.secret = req.secret
            userRepository.save(user)
        }

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Webhook updated",
            data = true
        )
    }

    fun deleteWebhook(userPk: String, id: Long): ResponseStatus<Boolean> {
        val user = userRepository.findById(userPk)
            .orElseThrow { IllegalArgumentException("User not found") }

        user.let {
            it.removeWebhook(id)
            userRepository.save(it)
        }

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Webhook deleted",
            data = true
        )
    }


}