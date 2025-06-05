package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.UserPasskeyDto
import com.ardi.afarensis.dto.response.ResponsePasskey
import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.*
import org.hibernate.annotations.DialectOverride.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.dialect.PostgreSQLDialect
import java.time.Instant

@Entity
@Table(name = "users_passkeys")
@SQLDelete(
    override = org.hibernate.annotations.SQLDelete(sql = "UPDATE users_passkeys SET deleted_at = NOW(), is_deleted = TRUE WHERE id = ?"),
    dialect = PostgreSQLDialect::class
)
@SQLRestriction("deleted_at IS NULL AND is_deleted = FALSE")
class UserPasskey(
    @Id
    @Column(length = 26)
    var id: String? = null,
    var userHandle: ByteArray = ByteArray(0),

    var credential: ByteArray = ByteArray(0),

    var publicKey: ByteArray = ByteArray(0),

    var deviceName: String = "",

    var lastUsedAt: Instant = Instant.now(),

    var createdAt: Instant = Instant.now(),

    var isDeleted: Boolean = false,

    var deletedAt: Instant? = null,

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

    fun toDto() = UserPasskeyDto(
        id = id!!,
        userHandle = userHandle,
        credential = credential,
        publicKey = publicKey,
        deviceName = deviceName,
        lastUsedAt = lastUsedAt,
    )

    fun toSummary() = ResponsePasskey.Summary(
        id!!, deviceName, lastUsedAt, createdAt
    )
}