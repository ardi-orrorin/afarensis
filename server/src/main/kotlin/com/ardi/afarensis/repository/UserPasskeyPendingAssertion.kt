package com.ardi.afarensis.repository

import com.ardi.afarensis.entity.UserPasskeyPendingAssertion
import org.springframework.data.jpa.repository.JpaRepository

interface UserPasskeyPendingAssertion : JpaRepository<UserPasskeyPendingAssertion, String> {
}