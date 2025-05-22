package com.ardi.afarensis.dto.response

import com.ardi.afarensis.dto.ResStatus

data class ResponseStatus<T>(
    val status: ResStatus,
    val message: String,
    val data: T? = null,
)
