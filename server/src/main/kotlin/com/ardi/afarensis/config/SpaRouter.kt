package com.ardi.afarensis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class SpaRouter {
    @Bean
    fun indexRouter(@Value("classpath:/static/index.html") html: Resource): RouterFunction<ServerResponse> {
        return RouterFunctions.route(
            RequestPredicates.GET("/").and(RequestPredicates.accept(MediaType.TEXT_HTML)), // 루트 경로 및 HTML 요청
            { _ -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html) }
        )
    }


    @Bean
    fun spaFallbackRouter(@Value("classpath:/static/index.html") html: Resource): RouterFunction<ServerResponse> {
        return RouterFunctions.route(
            RequestPredicates.GET("/{path:^(?!api|static|.*\\..*$).*$}/**")
                .and(RequestPredicates.accept(MediaType.TEXT_HTML)),
            { _ -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html) }
        )
    }
}

