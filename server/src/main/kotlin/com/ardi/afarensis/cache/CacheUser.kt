package com.ardi.afarensis.cache

import com.ardi.afarensis.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class CacheUser(
    private val userRepository: UserRepository
) {

    
}