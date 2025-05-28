package com.ardi.afarensis.dto

import java.time.Instant

data class UserRefreshTokenDto(
    var id: Long? = null,
    var refreshToken: String = "",
    var ip: String,
    var userAgent: String,
    var expiredAt: Instant = Instant.now(),
) {

}
