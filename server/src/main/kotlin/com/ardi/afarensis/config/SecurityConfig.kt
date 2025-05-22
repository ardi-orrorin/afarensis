package com.ardi.afarensis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.*
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.server.ServerWebExchange

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
class SecurityConfig : WebFluxConfigurer {

    @Bean
    fun springWebFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: ReactiveAuthenticationManager,
        authConverter: AuthConverter?
    ): SecurityWebFilterChain {
        val authenticationWebFilter = AuthenticationWebFilter(authenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(authConverter)

        return http
            .authorizeExchange { exchange: AuthorizeExchangeSpec -> this.authorizeExchange(exchange) }
            .formLogin { obj: FormLoginSpec -> obj.disable() }
            .httpBasic { obj: HttpBasicSpec -> obj.disable() }
            .csrf { obj: CsrfSpec -> obj.disable() }
            .cors { corsSpec: CorsSpec ->
                corsSpec.configurationSource(corsConfigurationSource())
            }
            .addFilterAfter(
                authenticationWebFilter,
                SecurityWebFiltersOrder.AUTHENTICATION
            )
            .build()
    }

    private fun authorizeExchange(exchange: AuthorizeExchangeSpec): AuthorizeExchangeSpec {
        return exchange
            .pathMatchers("/api/v1/public/**").permitAll()
            .pathMatchers("/api/v1/private/user/**").hasAuthority("USER")
            .pathMatchers("/api/v1/private/admin/**").hasAuthority("ADMIN")
            .pathMatchers("/api/v1/private/master/**").hasAuthority("ADMIN")
            .anyExchange().permitAll()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return CorsConfigurationSource { request: ServerWebExchange? ->
            val corsConfiguration = CorsConfiguration()
            corsConfiguration.allowedOrigins = listOf("http://localhost:8080", "https://localhost:8080")
            corsConfiguration.allowedMethods =
                listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            corsConfiguration.allowedHeaders = listOf("Authorization", "Content-Type")
            corsConfiguration.allowCredentials = true
            corsConfiguration.maxAge = 3600L
            corsConfiguration
        }
    }

}