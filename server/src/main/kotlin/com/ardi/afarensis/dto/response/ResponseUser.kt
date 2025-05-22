package com.ardi.afarensis.dto.response

class ResponseUser {

    class SignIn(
        val accessToken: String,
        val accessTokenExpiresIn: Long,
        val refreshToken: String,
        val refreshTokenExpiresIn: Long,
        val userId: String
    )
}