package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.OtpStatus
import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.UserDto
import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.*
import org.hibernate.annotations.DialectOverride.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.dialect.PostgreSQLDialect
import java.time.Instant

@Cacheable(value = true)
@Entity
@Table(name = "users")
@SQLDelete(
    override = org.hibernate.annotations.SQLDelete(sql = "UPDATE users SET deleted_at = NOW(), is_deleted = TRUE WHERE id = ?"),
    dialect = PostgreSQLDialect::class
)
@SQLRestriction("deleted_at IS NULL AND is_deleted = FALSE")
class User(
    @Id
    @Column(length = 26)
    var id: String? = null,
    var userId: String = "",
    var pwd: String = "",
    var email: String = "",
    var profileImg: String = "",
    var createdAt: Instant? = Instant.now(),
    var deletedAt: Instant? = null,

    var isDeleted: Boolean = false,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var userRoles: MutableSet<UserRole> = mutableSetOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var userRefreshToken: UserRefreshToken? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @SQLRestriction("available = true")
    var userVerifyEmails: MutableList<UserVerifyEmail> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var webhooks: MutableList<UserWebhook> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var webhookMessageLogs: MutableList<UserWebhookMessageLog> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var userPasskeys: MutableList<UserPasskey> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var userPasskeyPendingRegistrations: MutableList<UserPasskeyPendingRegistration> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var userPasskeyPendingAssertions: MutableList<UserPasskeyPendingAssertion> = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var otp: UserOtp? = null,
) {
    @PrePersist
    fun generateId() {
        if (id == null) {
            id = UlidCreator.getUlid().toString()
        }
    }


    fun toUserDetailDto() = UserDetailDto(
        id = id ?: "",
        pwd = pwd,
        userId = userId,
        profileImg = profileImg,
        roles = userRoles.map { it.role }.toMutableSet(),
    )

    fun toDto() = UserDto(
        id = id ?: "",
        pwd = pwd,
        userId = userId,
        email = email,
        profileImg = profileImg,
        roles = userRoles.map { it.role }.toMutableSet(),
        createdAt = createdAt ?: Instant.now(),
        userRefreshToken = userRefreshToken,
        webhooks = webhooks.map { it.toDto() }.toMutableSet(),
        webhookMessageLogs = webhookMessageLogs.map { it.toDto() }.toMutableList(),
    )

    fun addRole(roleType: com.ardi.afarensis.dto.Role) {
        val userRole = UserRole(role = roleType, user = this)
        userRoles.add(userRole)
    }


    fun addVerifyEmail(verifyCode: String) {
        userVerifyEmails.add(UserVerifyEmail(verifyKey = verifyCode, user = this))
    }

    fun addOtp(secretKey: String) {
        otp = UserOtp(hash = secretKey, status = OtpStatus.PENDING, user = this)
    }

}