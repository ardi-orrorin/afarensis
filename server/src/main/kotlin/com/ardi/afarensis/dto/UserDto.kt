package com.ardi.afarensis.dto

import java.time.Instant

data class UserDto(
    val id: Long,
    val userId: String,
    val email: String,
    val roles: MutableSet<Role> = mutableSetOf(),
    val profileImg: String,
    val createdAt: Instant,
)
