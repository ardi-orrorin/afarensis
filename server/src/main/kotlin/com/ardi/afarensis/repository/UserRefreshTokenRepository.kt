package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.UserRefreshToken
import org.springframework.data.jpa.repository.JpaRepository


interface UserRefreshTokenRepository : JpaRepository<UserRefreshToken, Long> {

    fun deleteByUserId(userId: String)

}