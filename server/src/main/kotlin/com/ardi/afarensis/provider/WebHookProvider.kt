package com.ardi.afarensis.provider

import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.webhook.Webhook
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class WebHookProvider(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val cacheSystemSetting: CacheSystemSetting,
) {
    fun discord(webhook: Webhook): Map<String, Any> {
        return sendMessage(webhook.url, webhook.toDiscord())
    }

    fun slack(webhook: Webhook): Map<String, Any> {
        return sendMessage(webhook.url, webhook.toSlack())
    }

    fun sendMessage(url: String, meessage: Any): Map<String, Any> {
        val sysWebhook = cacheSystemSetting.getSystemSetting()[SystemSettingKey.WEBHOOK]?.value
            ?: throw RuntimeException("Webhook not found")
        val enabled = sysWebhook["enabled"] as Boolean
        if (!enabled) return emptyMap()

        val jsonReq = objectMapper.writeValueAsString(meessage)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        restTemplate.exchange(
            url,
            HttpMethod.POST,
            HttpEntity(jsonReq, headers),
            String::class.java
        )

        val typeRef = object : TypeReference<Map<String, Any>>() {}
        return objectMapper.readValue(jsonReq, typeRef)
    }


}