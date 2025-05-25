package com.ardi.afarensis.dto.response


class ResponseUser {

    data class SignIn(
        val accessToken: String,
        val accessTokenExpiresIn: Long,
        val refreshToken: String,
        val refreshTokenExpiresIn: Long,
        val userId: String,
        val roles: Set<com.ardi.afarensis.dto.Role>
    )

    data class Role(
        val roles: Set<Role>
    )


}