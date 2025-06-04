package com.ardi.afarensis.service

import com.ardi.afarensis.dto.*
import com.ardi.afarensis.dto.request.RequestWebhook
import com.ardi.afarensis.dto.response.PageResponse
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.dto.response.ResponseWebhook
import com.ardi.afarensis.dto.webhook.Webhook
import com.ardi.afarensis.entity.User
import com.ardi.afarensis.entity.UserWebhook
import com.ardi.afarensis.entity.UserWebhookMessageLog
import com.ardi.afarensis.provider.WebHookProvider
import com.ardi.afarensis.repository.UserWebhookMessageLogRepository
import com.ardi.afarensis.repository.UserWebhookRepository
import com.ardi.afarensis.util.toResponse
import kotlinx.coroutines.*
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WebhookService(
    private val webhookRepository: UserWebhookRepository,
    private val userWebhookMessageLogRepository: UserWebhookMessageLogRepository,
    private val webHookProvider: WebHookProvider,
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
        val sysWebhook = getCacheSystemSettingKey(SystemSettingKey.WEBHOOK)?.value
            ?: throw IllegalArgumentException("Webhook not found")

        val user = userRepository.findById(userPk)
            .orElseThrow { IllegalArgumentException("User not found") }

        val hasRole = sysWebhook["hasRole"] as List<String>


        val hasIntersection = hasRole.all { role -> role in user.userRoles.map { it.role.name } }
        if (!hasIntersection) {
            throw IllegalArgumentException("사용할 권한이 없습니다.")
        }

        // Send Message Test
        runBlocking {
            try {
                routeWebhook(req.type, req.url)
            } catch (e: Exception) {
                throw IllegalArgumentException("Webhook url is not valid")
            }
        }

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

    suspend fun sendWebhookMessageByCoverageWithRoles(
        coverage: Coverage, user: User, title: String, content: String, path: String
    ) = withContext(Dispatchers.IO) {
        val sysWebhook = getCacheSystemSettingKey(SystemSettingKey.WEBHOOK)?.value
            ?: throw RuntimeException("System not Init value")

        val enabled = sysWebhook["enabled"] as Boolean
        if (!enabled) return@withContext

        val hasRole = sysWebhook["hasRole"] as List<String>
        val coverages = sysWebhook["coverage"] as List<String>

        if (!coverages.contains(coverage.name)) return@withContext

        val userDto = user.toDto()

        val hasUserRoles = hasRole.all { role -> role in userDto.roles.map { it.name } }

        if (!hasUserRoles) return@withContext
        if (userDto.webhooks.isEmpty()) return@withContext

        val jsonReq = userDto.webhooks.map {
            val webhook = Webhook(
                url = it.url,
                title = title,
                content = content,
                path = path,
            )

            Pair(it.type, webhook)
        }.map {
            async {
                val webhookType = it.first
                val webhook = it.second
                routeWebhook(webhookType, webhook)
            }
        }.awaitAll().first()

        user.webhooks.map {
            UserWebhookMessageLog(
                message = jsonReq,
                user = it.user!!,
                userWebhooks = it
            )
        }.let {
            userWebhookMessageLogRepository.saveAll(it)
        }
    }

    suspend fun routeWebhook(type: WebhookType, url: String): Map<String, Any> {
        val sysInit = systemSetting.getSystemSetting()[SystemSettingKey.INIT]?.value
            ?: throw IllegalArgumentException("Default path not found")

        val defaultPath = sysInit["homeUrl"] as String

        val testData = Webhook(
            url,
            "$type connect test",
            "This is a test message from $type webhook",
            defaultPath,
        )

        return routeWebhook(type, testData)
    }

    suspend fun routeWebhook(type: WebhookType, webhook: Webhook): Map<String, Any> {
        return when (type) {
            WebhookType.SLACK -> webHookProvider.slack(webhook)
            WebhookType.DISCORD -> webHookProvider.discord(webhook)
            else -> throw IllegalArgumentException("Webhook type not found")
        }
    }


    fun User.addWebhook(webhook: UserWebhook) {
        webhooks.add(webhook)
        webhook.user = this
    }

    fun User.removeWebhook(id: Long) {
        val webhook = webhooks.find { it.id == id } ?: throw IllegalArgumentException("Webhook not found")
        webhooks.remove(webhook)
        webhook.user = null
    }
}