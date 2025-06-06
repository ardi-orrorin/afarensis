package com.ardi.afarensis.exception

import com.ardi.afarensis.controller.BasicController
import com.ardi.afarensis.util.CookieUtil
import kotlinx.coroutines.runBlocking
import org.apache.hc.client5.http.auth.AuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class ExceptionAdviceController(
    private val cookieUtil: CookieUtil,
) : BasicController() {


    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorizedException(ex: UnauthorizedException, res: ServerHttpResponse): ErrorResponse = runBlocking {
        cookieUtil.removeCookie(res)

        ErrorResponse(
            message = ex.message ?: "Unauthorized",
            errorCode = "UNAUTHORIZED",
            timestamp = LocalDateTime.now(),
            details = mapOf("additionalInfo" to "Some additional info")
        )
    }

    @ExceptionHandler(UnSignRefreshTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnSignRefreshTokenException(ex: UnSignRefreshTokenException, res: ServerHttpResponse): ErrorResponse =
        runBlocking {
            cookieUtil.removeCookie(res)

            ErrorResponse(
                message = ex.message ?: "UnSignRefreshToken",
                errorCode = "UNAUTHORIZED",
                timestamp = LocalDateTime.now()
            )
        }

    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(ex: AuthenticationException, res: ServerHttpResponse): ErrorResponse =
        runBlocking {
            cookieUtil.removeCookie(res)

            ErrorResponse(
                message = ex.message ?: "Authentication failed",
                errorCode = "AUTHENTICATION_FAILED",
                timestamp = LocalDateTime.now()
            )
        }
}

data class ErrorResponse(
    val message: String,
    val errorCode: String,
    val timestamp: LocalDateTime,
    val details: Map<String, Any>? = null
)