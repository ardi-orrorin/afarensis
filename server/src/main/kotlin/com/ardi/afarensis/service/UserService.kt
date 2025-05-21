package com.ardi.afarensis.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional
class UserService: ReactiveUserDetailsService {

    override fun findByUsername(username: String?): Mono<UserDetails> {
        TODO("Not yet implemented")
    }
}