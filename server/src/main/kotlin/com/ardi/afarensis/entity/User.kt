package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.UserDetailDto
import com.ardi.afarensis.dto.UserDto
import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var userId: String = "",
    var pwd: String = "",
    var email: String = "",
    var profileImg: String = "",
    var createdAt: Instant? = Instant.now(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var userRoles: MutableSet<UserRole> = mutableSetOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var userRefreshToken: UserRefreshToken? = null
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