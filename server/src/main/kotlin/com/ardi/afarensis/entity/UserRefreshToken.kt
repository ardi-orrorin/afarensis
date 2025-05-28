package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.UserRefreshTokenDto
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users_refresh_token")
class UserRefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var refreshToken: String = "",

    var ip: String,

    var userAgent: String,

    var expiredAt: Instant = Instant.now(),

    @OneToOne
    @JoinColumn(name = "users_pk", insertable = true, updatable = true, nullable = true)
    var user: User? = null,
) {

    fun toDto() = UserRefreshTokenDto(
        id = id,
        refreshToken = refreshToken,
        ip = ip,
        userAgent = userAgent,
        expiredAt = expiredAt,
    )
}