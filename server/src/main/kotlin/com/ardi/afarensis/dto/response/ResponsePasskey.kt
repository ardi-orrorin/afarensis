package com.ardi.afarensis.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

class ResponsePasskey {

    data class Summary(
        val id: String,
        val deviceName: String,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        val lastUsedAt: Instant,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        val createdAt: Instant
    )

    data class SummaryList(
        val data: MutableList<Summary>
    )
}