package com.ardi.afarensis.dto

import com.ardi.afarensis.entity.UserRefreshToken
import java.time.Instant

data class UserDto(
    val id: Long,
    val pwd: String,
    val userId: String,
    val email: String,
    val roles: MutableSet<Role> = mutableSetOf(),
    val profileImg: String,
    val createdAt: Instant,
    var userRefreshToken: UserRefreshToken? = null,
)
