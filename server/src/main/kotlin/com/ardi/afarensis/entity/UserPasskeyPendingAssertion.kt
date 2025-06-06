package com.ardi.afarensis.entity

import com.ardi.afarensis.entity.converter.JsonToPublicAssertionRequest
import com.github.f4b6a3.ulid.UlidCreator
import com.yubico.webauthn.AssertionRequest
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "users_passkeys_pending_assertions")
//@SQLRestriction("expired_at > now()") fixme: 쿼리는 문제 없는데 찾지 못함
class UserPasskeyPendingAssertion(
    @Id
    var id: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false, updatable = false)
    @Convert(converter = JsonToPublicAssertionRequest::class)
    var request: AssertionRequest,
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