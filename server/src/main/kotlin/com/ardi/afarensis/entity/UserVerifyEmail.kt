package com.ardi.afarensis.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "users_verify_email")
class UserVerifyEmail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var verifyKey: String = "",
    var available: Boolean = true,
    var expiredAt: Instant = Instant.now().plus(5, ChronoUnit.MINUTES),

    @ManyToOne
    @JoinColumn(name = "users_pk", updatable = true, insertable = true)
    var user: User
) {

}