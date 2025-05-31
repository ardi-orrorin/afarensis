package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.User
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<User, String> {
    fun findByUserId(username: String): User?
    fun existsByUserId(userId: String): Boolean
}