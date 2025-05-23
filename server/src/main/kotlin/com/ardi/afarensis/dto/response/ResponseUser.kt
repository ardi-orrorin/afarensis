package com.ardi.afarensis.dto.response

import com.ardi.afarensis.dto.Role

class ResponseUser {

    class SignIn(
        val accessToken: String,
        val accessTokenExpiresIn: Long,
        val refreshToken: String,
        val refreshTokenExpiresIn: Long,
        val userId: String,
        val roles: Set<Role>
    )
}