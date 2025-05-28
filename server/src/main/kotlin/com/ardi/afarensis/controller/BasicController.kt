package com.ardi.afarensis.controller

import com.ardi.afarensis.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import java.time.Duration

open class BasicController {

    @Autowired
    lateinit var userService: UserService

    suspend fun removeCookie(res: ServerHttpResponse) = withContext(Dispatchers.Default) {
        listOf("access_token", "refresh_token", "user_id", "roles").map {
            async {
                res.addCookie(createResponseCookie(ResponseCookieEntity(it, "", 0)))
            }
        }.awaitAll()
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

    data class ResponseCookieEntity(
        val name: String,
        val value: String,
        val expiresIn: Long,
        val httpOnly: Boolean = true
    )

    fun getIpAndUserAgent(req: ServerHttpRequest): Pair<String, String> {
        val ip = req.remoteAddress?.address?.hostAddress ?: ""
        val userAgent = req.headers["User-Agent"]?.first() ?: ""

        return Pair(ip, userAgent)
    }
}