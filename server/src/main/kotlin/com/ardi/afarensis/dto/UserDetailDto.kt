package com.ardi.afarensis.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailDto(
    val id: Long,
    val userId: String,
    val pwd: String,
    val roles: List<Role>,
    val profileImg: String
): UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> {
        return roles.map { role -> GrantedAuthority { role.toString() }}
            .toList()
    }

    override fun getPassword(): String {
        return this.pwd;
    }

    override fun getUsername(): String {
        return this.userId;
    }

    override fun isAccountNonExpired(): Boolean {
        return super.isAccountNonExpired()
    }

    override fun isAccountNonLocked(): Boolean {
        return super.isAccountNonLocked()
    }

    override fun isCredentialsNonExpired(): Boolean {
        return super.isCredentialsNonExpired()
    }

    override fun isEnabled(): Boolean {
        return super.isEnabled()
    }
}
