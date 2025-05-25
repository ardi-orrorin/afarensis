package com.ardi.afarensis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*

@Configuration
class SpaRouter {
    data class StaticResourceMapping(val urlPattern: String, val resourceLocation: String)

    @Bean("staticResourceRouter")
    @Order(1)
    fun staticResourceRouter(): RouterFunction<ServerResponse> {
        val resourceMappings = listOf(
            StaticResourceMapping("/static/**", "static/static/"),
            StaticResourceMapping("/mocks/**", "static/mocks/"),
        )

        val neverPredicate = RequestPredicate { request: ServerRequest -> false }

        return resourceMappings.map { mapping ->
            RouterFunctions.resources(mapping.urlPattern, ClassPathResource(mapping.resourceLocation))
        }.reduceOrNull { acc, next -> acc.and(next) }
            ?: RouterFunctions.route(neverPredicate) { ServerResponse.notFound().build() } // 리스트가 비었을 경우
    }


    @Bean("customSpaRouter")
    @Order(2)
    fun spaRouter(): RouterFunction<ServerResponse> {
        return RouterFunctions.route(
            RequestPredicates.GET("/**")
                .and(RequestPredicates.accept(MediaType.TEXT_HTML))
                .and(RequestPredicates.path("/api/**").negate())
                .and(RequestPredicates.path("/static/**").negate()),
            { _ ->
                val indexHtml = ClassPathResource("static/index.html")
                ServerResponse.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .bodyValue(indexHtml)
            }
        )
    }

    @Bean
    fun spaFallbackRouter(@Value("classpath:/static/index.html") html: Resource): RouterFunction<ServerResponse> {
        return RouterFunctions.route(
            RequestPredicates.GET("/{path:^(?!api|static|.*\\.[a-zA-Z0-9]+$).*$}")
                .and(RequestPredicates.accept(MediaType.TEXT_HTML)),
            { _ -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html) }
        )
    }
}