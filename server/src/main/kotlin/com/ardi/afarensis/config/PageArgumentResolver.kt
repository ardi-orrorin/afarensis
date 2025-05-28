package com.ardi.afarensis.config

import com.ardi.afarensis.dto.request.RequestPage
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class PageArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterType().equals(RequestPage::class)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        val params = exchange.getRequest().getQueryParams()

        val page = RequestPage(
            params.getFirst("page")?.toInt() ?: 0,
            params.getFirst("size")?.toInt() ?: 10,
            params.getFirst("sortBy") ?: "id",
            params.getFirst("sortDirection") ?: "desc",
            params.getFirst("search") ?: ""
        );


        return Mono.just(page);
    }
}
