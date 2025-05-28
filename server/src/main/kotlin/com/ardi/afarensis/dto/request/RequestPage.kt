package com.ardi.afarensis.dto.request

import com.ardi.afarensis.util.toZeroBasedPage
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class RequestPage(
    val page: Int,
    val size: Int,
    val sortBy: String,
    val sortDirection: String,
    val search: String
) {

    fun toPageRequest() =
        PageRequest.of(
            this.page.toZeroBasedPage(),
            this.size,
            Sort.by(this.sortBy, this.sortDirection)
        )
}