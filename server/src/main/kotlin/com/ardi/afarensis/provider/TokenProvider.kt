package com.ardi.afarensis.provider

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ClaimsBuilder
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant
import javax.crypto.SecretKey

@Component
class TokenProvider {
    @Value("\${jwt.secret}")
    private val secret: String? = null

    private var SECRET_KEY: SecretKey? = null

    // 일 * 시 * 분 * 초
    val ACCESS_EXP: Long = (24 * 60 * 60).toLong()
    val REFRESH_EXP: Long = (30 * 24 * 60 * 60).toLong()

    fun generateToken(userId: String?, isRefresh: Boolean): String {
        SECRET_KEY = Keys.hmacShaKeyFor(secret!!.toByteArray())
        val exp = if (isRefresh) REFRESH_EXP else ACCESS_EXP
        val type = if (isRefresh) "refresh" else "access"
        val claims: ClaimsBuilder = Jwts.claims()
        claims.add("user", userId)
        claims.add("type", type)

        return Jwts.builder()
            .claims(claims.build())
            .expiration(
                Timestamp(Instant.now().toEpochMilli() + exp * 1000)
            )
            .signWith(SECRET_KEY)
            .compact()
    }

    fun getClaims(token: String?): Claims {
        SECRET_KEY = Keys.hmacShaKeyFor(secret!!.toByteArray())
        val jwts: JwtParser = Jwts.parser()
            .verifyWith(SECRET_KEY)
            .build()
        return jwts.parseSignedClaims(token)
            .getPayload()
    }
}