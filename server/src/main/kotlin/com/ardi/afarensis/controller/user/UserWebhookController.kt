package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.request.RequestPage
import com.ardi.afarensis.dto.request.RequestWebhook
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.dto.response.ResponseWebhook
import com.ardi.afarensis.service.WebhookService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/user/webhook")
class UserWebhookController(
    private val webhookService: WebhookService
) : BasicController() {

    @GetMapping("")
    suspend fun findAllByUserId(
        @AuthenticationPrincipal principal: UserDetailDto
    ): ResponseWebhook.List {
        validAuthentication(principal)
        return webhookService.findAllByUserId(principal.id)
    }

    @PostMapping("")
    suspend fun saveWebhook(
        @AuthenticationPrincipal principal: UserDetailDto,
        @Valid @RequestBody req: RequestWebhook.SaveWebhook
    ): ResponseStatus<Boolean> {
        validAuthentication(principal)
        return webhookService.saveWebhook(principal.id, req)
    }

    @PatchMapping("")
    suspend fun updateWebhook(
        @AuthenticationPrincipal principal: UserDetailDto,
        @Valid @RequestBody req: RequestWebhook.SaveWebhook
    ): ResponseStatus<Boolean> {
        validAuthentication(principal)
        return webhookService.updateWebhook(principal.id, req)
    }

    @DeleteMapping("{id}")
    suspend fun deleteWebhook(
        @AuthenticationPrincipal principal: UserDetailDto,
        @PathVariable id: Long
    ): ResponseStatus<Boolean> {
        validAuthentication(principal)
        return webhookService.deleteWebhook(principal.id, id)
    }

    @GetMapping("log")
    suspend fun findLogByUserId(
        @AuthenticationPrincipal principal: UserDetailDto,
        page: RequestPage
    ) = webhookService.findMessageLogByUserPk(principal.id, page.toPageRequest())


    fun validAuthentication(principal: UserDetailDto) {
        val sysWebhook = cacheSystemSettingService.getCacheSystemSettingKey(SystemSettingKey.WEBHOOK)?.value
            ?: throw IllegalArgumentException("Webhook not found")


        val hasRole = sysWebhook["hasRole"] as List<String>

        val hasIntersection = hasRole.intersect(principal.roles.map { it.name }.toSet()).size == hasRole.size

        if (!hasIntersection) {
            throw IllegalArgumentException("사용할 권한이 없습니다.")
        }
    }

}