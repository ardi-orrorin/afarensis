package com.ardi.afarensis.dto.response

import java.time.Instant

class ResponsePasskey {

    data class Summary(
        val id: String,
        val deviceName: String,
        val lastUsedAt: Instant,
        val createdAt: Instant
    )

    data class SummaryList(
        val data: MutableList<Summary>
    )
}