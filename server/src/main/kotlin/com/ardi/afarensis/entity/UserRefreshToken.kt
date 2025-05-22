package com.ardi.afarensis.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users_refresh_token")
class UserRefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var refreshToken: String = "",

    var expiredAt: Instant = Instant.now(),

    @OneToOne
    @JoinColumn(name = "users_pk", insertable = true, updatable = true, nullable = true)
    var user: User? = null,
) {
}