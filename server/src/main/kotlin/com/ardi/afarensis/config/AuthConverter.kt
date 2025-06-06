package com.ardi.afarensis.config

import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.provider.TokenProvider
import com.ardi.afarensis.service.UserService
import com.ardi.afarensis.util.CookieUtil
import io.jsonwebtoken.Claims
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import java.util.*

@Component
class AuthConverter(
    private val tokenProvider: TokenProvider,
    private val cookieUtil: CookieUtil,
    private val mutex: Mutex,
    private val userService: UserService
) : ServerAuthenticationConverter {
    val ACCESS_COOKIE_NAME = "access_token"
    val REFRESH_COOKIE_NAME = "refresh_token"

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val (accessToken, refreshToken, userId) = findTokens(exchange.request)

        if (userId.isEmpty() || refreshToken.isEmpty()) {
            return Mono.empty<Authentication?>()
                .doFinally { signalType ->
                    if (signalType != SignalType.ON_COMPLETE) return@doFinally

                    CoroutineScope(Dispatchers.Default).launch {
                        cookieUtil.removeCookie(exchange.response)
                    }
                }
        }

        if (accessToken.isEmpty()) {
            tokenRefresh(accessToken, refreshToken, userId, exchange)
            val newAccessToken = exchange.response.cookies[ACCESS_COOKIE_NAME]
                ?.firstOrNull()
                ?.value

            return isValidateToken(newAccessToken)
        }

        return isValidateToken(accessToken)
    }

    fun findTokens(request: ServerHttpRequest): Triple<String, String, String> {
        val cookies = request.headers.getOrEmpty("Cookie")

        if (cookies.isEmpty()) return Triple("", "", "")

        val tokens = findTokensByCookies(cookies)

        if (tokens.isEmpty()) return Triple("", "", "")

        val accessToken = tokens[ACCESS_COOKIE_NAME] ?: ""
        val refreshToken = tokens[REFRESH_COOKIE_NAME] ?: ""
        val userId = tokens["user_id"] ?: ""

        return Triple(accessToken, refreshToken, userId)
    }

    fun isValidateToken(token: String?): Mono<Authentication> {
        val claims: Claims = tokenProvider.getClaims(token)
        val userId: String = claims.get("user", String::class.java)

        return userService.findByUsername(userId)
            .onErrorMap { RuntimeException("유저 정보가 없습니다.") }
            .map {
                UsernamePasswordAuthenticationToken(it, token, it.authorities)
            }
    }

    fun findTokensByCookies(cookies: List<String>): Map<String, String> {
        for (cookie in cookies) {
            if (cookie.contains(ACCESS_COOKIE_NAME + "=") || cookie.contains(REFRESH_COOKIE_NAME + "=")) {
                val tokens: Map<String, String> = cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                    .associate {
                        it.split("=").let {
                            it[0].trim() to it[1]
                        }
                    }
                return tokens
            }
        }

        return mutableMapOf()
    }

    fun tokenRefresh(accessToken: String, refreshToken: String, userId: String, exchange: ServerWebExchange) {
        if (accessToken.isNotEmpty() || refreshToken.isEmpty() || userId.isEmpty()) return;

        val request = exchange.request

        val decodedUserId = Base64.getDecoder()
            .decode(userId)
            .toString(Charsets.UTF_8)


        val (ip, userAgent) = cookieUtil.getIpAndUserAgent(request)

        val req = RequestUser.RefreshToken(
            refreshToken,
            decodedUserId,
            ip,
            userAgent
        )

        val signInfo = userService.publishAccessToken(req)

        val base64Roles =
            Base64.getEncoder()
                .encode(signInfo.roles.joinToString(":").encodeToByteArray())
                .toString(Charsets.UTF_8)

        listOf(
            CookieUtil.ResponseCookieEntity("access_token", signInfo.accessToken, signInfo.accessTokenExpiresIn, true),
            CookieUtil.ResponseCookieEntity(
                "user_id", userId, signInfo.refreshTokenExpiresIn, false
            ),
            CookieUtil.ResponseCookieEntity(
                "roles",
                base64Roles,
                signInfo.refreshTokenExpiresIn,
                false
            )
        ).forEach {
            exchange.response.addCookie(
                cookieUtil.createResponseCookie(it)
            )
        }
    }


}