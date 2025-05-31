package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.UserDto
import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.*
import org.hibernate.annotations.DialectOverride.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.dialect.PostgreSQLDialect
import java.time.Instant


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

    fun addRefreshToken(refreshToken: String, ip: String, agent: String, expiredAt: Instant) {
        userRefreshToken =
            UserRefreshToken(
                refreshToken = refreshToken,
                expiredAt = expiredAt,
                ip = ip,
                userAgent = agent,
                user = this
            )
    }

    fun addVerifyEmail(verifyCode: String) {
        userVerifyEmails.add(UserVerifyEmail(verifyKey = verifyCode, user = this))
    }

    fun removeRefreshToken() {
        userRefreshToken = null
    }

    fun addWebhook(webhook: UserWebhook) {
        webhooks.add(webhook)
        webhook.user = this
    }

    fun removeWebhook(id: Long) {
        val webhook = webhooks.find { it.id == id } ?: throw IllegalArgumentException("Webhook not found")
        webhooks.remove(webhook)
        webhook.user = null
    }

}