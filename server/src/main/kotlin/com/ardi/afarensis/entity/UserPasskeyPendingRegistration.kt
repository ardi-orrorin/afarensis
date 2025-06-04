package com.ardi.afarensis.entity

import com.ardi.afarensis.entity.converter.JsonToPublicKeyCredentialCreationOptions
import com.github.f4b6a3.ulid.UlidCreator
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.SQLRestriction
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "users_passkeys_pending_registrations")
@SQLRestriction("expired_at > now()")
class UserPasskeyPendingRegistration(
    @Id
    var id: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false, updatable = false)
    @Convert(converter = JsonToPublicKeyCredentialCreationOptions::class)
    var options: PublicKeyCredentialCreationOptions,
    var createdAt: Instant = Instant.now(),
    var expiredAt: Instant = Instant.now().plus(5, ChronoUnit.MINUTES),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_pk", insertable = true, updatable = true)
    var user: User? = null
) {
    @PrePersist
    fun generateId() {
        if (id == null) {
            id = UlidCreator.getUlid().toString()
        }
    }
}