package com.ardi.afarensis.provider

import com.ardi.afarensis.dto.webhook.Webhook
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class WebHookProvider(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {
    suspend fun discord(webhook: Webhook) {
        sendMessage(webhook.url, webhook.toDiscord())
    }

    suspend fun slack(webhook: Webhook) {
        sendMessage(webhook.url, webhook.toSlack())
    }

    suspend fun sendMessage(url: String, meessage: Any) = withContext(Dispatchers.IO) {
        val jsonReq = objectMapper.writeValueAsString(meessage)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        restTemplate.exchange(
            url,
            HttpMethod.POST,
            HttpEntity(jsonReq, headers),
            Void::class.java
        )
    }


}