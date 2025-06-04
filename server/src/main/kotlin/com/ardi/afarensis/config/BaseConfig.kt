package com.ardi.afarensis.config

import kotlinx.coroutines.sync.Mutex
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import kotlin.random.Random

@Configuration
@EnableCaching
@EnableJpaRepositories("com.ardi.afarensis.repository")
class BaseConfig {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun transactionTemplate(transactionManager: PlatformTransactionManager) =
        TransactionTemplate(transactionManager)

    @Bean
    fun restTemplate() = org.springframework.web.client.RestTemplate()

    @Bean
    fun mutex() = Mutex()

    @Bean
    fun random() = Random
}