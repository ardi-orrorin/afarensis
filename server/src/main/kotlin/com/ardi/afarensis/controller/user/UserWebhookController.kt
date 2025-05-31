package com.ardi.afarensis.controller.user

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.UserWebhookMessageLogDto
import com.ardi.afarensis.dto.request.RequestPage
import com.ardi.afarensis.dto.request.RequestWebhook
import com.ardi.afarensis.dto.response.PageResponse
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
        return webhookService.findAllByUserId(principal.id)
    }

    @PostMapping("")
    suspend fun saveWebhook(
        @AuthenticationPrincipal principal: UserDetailDto,
        @Valid @RequestBody req: RequestWebhook.SaveWebhook
    ): ResponseStatus<Boolean> {
        return webhookService.saveWebhook(principal.id, req)
    }

    @PatchMapping("")
    suspend fun updateWebhook(
        @AuthenticationPrincipal principal: UserDetailDto,
        @Valid @RequestBody req: RequestWebhook.SaveWebhook
    ): ResponseStatus<Boolean> {
        return webhookService.updateWebhook(principal.id, req)
    }

    @DeleteMapping("{id}")
    suspend fun deleteWebhook(
        @AuthenticationPrincipal principal: UserDetailDto,
        @PathVariable id: Long
    ): ResponseStatus<Boolean> {
        return webhookService.deleteWebhook(principal.id, id)
    }

    @GetMapping("log")
    suspend fun findLogByUserId(
        @AuthenticationPrincipal principal: UserDetailDto,
        page: RequestPage
    ): PageResponse<UserWebhookMessageLogDto> {
        return webhookService.findMessageLogByUserPk(principal.id, page.toPageRequest())
    }

}