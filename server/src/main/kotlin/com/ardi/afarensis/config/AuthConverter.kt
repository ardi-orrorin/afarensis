package com.ardi.afarensis.config

import com.ardi.afarensis.provider.TokenProvider
import com.ardi.afarensis.service.UserService
import io.jsonwebtoken.Claims
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthConverter(
    val tokenProvider: TokenProvider,
    val userService: UserService,
) : ServerAuthenticationConverter {
    val ACCESS_COOKIE_NAME = "access_token"
    val REFRESH_COOKIE_NAME = "refresh_token"
    val COOKIE_NAME = "Bearer"

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val headers = exchange.request.headers
        var auth = headers.getFirst("Authorization")
        val cookies = headers.getOrEmpty("Cookie")

        if (auth != null && auth.startsWith(COOKIE_NAME + " ")) {
            auth = auth.substring(COOKIE_NAME.length + 1)
            return isValidateToken(auth)
        }

        if (!cookies.isEmpty()) {
            for (cookie in cookies) {
                if (cookie.startsWith(ACCESS_COOKIE_NAME + "=") || cookie.startsWith(REFRESH_COOKIE_NAME + "=")) {
                    val tokens = cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val accessToken = Arrays.stream<String>(tokens)
                        .filter { c: String -> c.contains(ACCESS_COOKIE_NAME) }.findFirst().orElse("")
//                    val refreshToken = Arrays.stream<String>(tokens)
//                        .filter { c: String -> c.contains(REFRESH_COOKIE_NAME) }.findFirst().orElse("")

                    if (!accessToken.isEmpty()) {
                        return isValidateToken(accessToken.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1])
                    }
//                    else if (!refreshToken.isEmpty()) {
//                        return isValidateToken(refreshToken.split("=".toRegex()).dropLastWhile { it.isEmpty() }
//                            .toTypedArray()[1])
//                    }
                }
            }
        }

        return Mono.empty()
    }

    fun isValidateToken(token: String?): Mono<Authentication> {
        val claims: Claims = tokenProvider.getClaims(token)
        val userId: String = claims.get("user", String::class.java)

        return userService.findByUsername(userId)
            .onErrorMap { e -> RuntimeException("유저 정보가 없습니다.") }
            .map { u -> UsernamePasswordAuthenticationToken(u, token, u.getAuthorities()) }
    }
}