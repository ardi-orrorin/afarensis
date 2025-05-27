package com.ardi.afarensis.dto.request

data class RequestPage(
    val page: Int,
    val size: Int,
    val sortBy: String,
    val sortDirection: String,
    val search: String
) {
}