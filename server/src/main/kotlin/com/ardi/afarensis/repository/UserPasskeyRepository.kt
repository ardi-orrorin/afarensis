package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.UserPasskey
import org.springframework.data.jpa.repository.JpaRepository

interface UserPasskeyRepository : JpaRepository<UserPasskey, String> {
    fun findByCredentialAndUserHandle(credential: ByteArray, userHandle: ByteArray): UserPasskey?
    fun findAllByCredential(credential: ByteArray): MutableList<UserPasskey>
    fun findFirstByUserHandle(userHandle: ByteArray): UserPasskey?
}