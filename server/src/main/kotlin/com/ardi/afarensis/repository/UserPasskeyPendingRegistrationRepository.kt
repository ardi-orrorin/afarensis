package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.UserPasskeyPendingRegistration
import org.springframework.data.jpa.repository.JpaRepository

interface UserPasskeyPendingRegistrationRepository : JpaRepository<UserPasskeyPendingRegistration, String> {
}