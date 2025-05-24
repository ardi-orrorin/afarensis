package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.UserDto
import jakarta.persistence.*
import org.hibernate.annotations.DialectOverride.SQLDelete
import org.hibernate.annotations.DialectOverride.Where
import org.hibernate.dialect.PostgreSQLDialect
import java.time.Instant


@Entity
@Table(name = "users")
@Where(
    override = org.hibernate.annotations.Where(clause = "deleted_at IS NULL AND is_deleted = FALSE"),
    dialect = PostgreSQLDialect::class
)
@SQLDelete(
    override = org.hibernate.annotations.SQLDelete(sql = "UPDATE users SET deleted_at = NOW(), is_deleted = TRUE WHERE id = ?"),
    dialect = PostgreSQLDialect::class
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    var userId: String = "",
    var pwd: String = "",
    var email: String = "",
    var profileImg: String = "",
    var createdAt: Instant? = Instant.now(),
    var deletedAt: Instant? = null,
    var isDeleted: Boolean = false,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var userRoles: MutableSet<UserRole> = mutableSetOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var userRefreshToken: UserRefreshToken? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)

    @Where(override = org.hibernate.annotations.Where(clause = "available = true"), dialect = PostgreSQLDialect::class)
    var userVerifyEmails: MutableList<UserVerifyEmail> = mutableListOf(),
) {
    fun toUserDetailDto() = UserDetailDto(
        id = id,
        pwd = pwd,
        userId = userId,
        profileImg = profileImg,
        roles = userRoles.map { it.role }.toMutableSet(),
    )

    fun toDto() = UserDto(
        id = id,
        userId = userId,
        email = email,
        profileImg = profileImg,
        roles = userRoles.map { it.role }.toMutableSet(),
        createdAt = createdAt ?: Instant.now()
    )

    fun addRole(roleType: com.ardi.afarensis.dto.Role) {
        val userRole = UserRole(role = roleType, user = this)
        userRoles.add(userRole)
    }

    fun addRefreshToken(refreshToken: String, expiredAt: Instant) {
        userRefreshToken = UserRefreshToken(refreshToken = refreshToken, expiredAt = expiredAt, user = this)
    }

    fun removeRefreshToken() {
        userRefreshToken = null
    }

}