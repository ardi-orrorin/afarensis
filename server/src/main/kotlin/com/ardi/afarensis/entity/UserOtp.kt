package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.OtpStatus
import jakarta.persistence.*
import org.hibernate.annotations.DialectOverride.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.dialect.PostgreSQLDialect
import java.time.Instant

@Entity
@Table(name = "users_otps")
@SQLDelete(
    override = org.hibernate.annotations.SQLDelete(sql = "UPDATE users_otps SET deleted_at = NOW(), is_deleted = TRUE WHERE id = ?"),
    dialect = PostgreSQLDialect::class
)
@SQLRestriction("deleted_at IS NULL AND is_deleted = FALSE")
class UserOtp(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var hash: String,

    @Enumerated(EnumType.STRING)
    var status: OtpStatus,

    var createdAt: Instant = Instant.now(),
    var lastUsedAt: Instant = Instant.now(),
    var isDeleted: Boolean = false,
    var deletedAt: Instant? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_pk", insertable = true, updatable = true)
    var user: User?
) {

}