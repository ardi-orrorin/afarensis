package com.ardi.afarensis.util

import com.ardi.afarensis.dto.response.PageResponse
import org.springframework.data.domain.Page

fun Int.toZeroBasedPage(): Int = maxOf(0, this - 1)
fun Int.toOneBasedPage(): Int = this + 1

fun <T> Page<*>.toResponse(data: List<T>): PageResponse<T> {
    return PageResponse(
        data = data,
        page = this.number.toOneBasedPage(),
        size = this.size,
        total = this.totalElements,
        totalPages = this.totalPages,
        isFirst = this.isFirst,
        isLast = this.isLast,
        hasNext = this.hasNext(),
        hasPrevious = this.hasPrevious()
    )
}

