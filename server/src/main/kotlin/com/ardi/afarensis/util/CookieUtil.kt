package com.ardi.afarensis.util

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CookieUtil(
    private val mutex: Mutex
) {
    fun getIpAndUserAgent(req: ServerHttpRequest): Pair<String, String> {
        val ip = req.remoteAddress?.address?.hostAddress ?: ""
        val userAgent = req.headers["User-Agent"]?.first() ?: ""

        return Pair(ip, userAgent)
    }

    fun createResponseCookie(cookie: ResponseCookieEntity): ResponseCookie {
        return ResponseCookie.from(cookie.name, cookie.value)
            .path("/")
            .maxAge(if (cookie.expiresIn == 0L) Duration.ZERO else Duration.ofSeconds(cookie.expiresIn))
            .httpOnly(cookie.httpOnly)
            .secure(true)
            .sameSite("Strict")
            .partitioned(true)
            .build()
    }

    suspend fun removeCookie(res: ServerHttpResponse) = supervisorScope {
        listOf("access_token", "refresh_token", "user_id", "roles").map {
            async {
                mutex.withLock {
                    res.addCookie(createResponseCookie(ResponseCookieEntity(it, "", 0)))
                }
            }
        }.awaitAll()
    }

    data class ResponseCookieEntity(
        val name: String,
        val value: String,
        val expiresIn: Long,
        val httpOnly: Boolean = true
    )
}