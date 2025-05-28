package com.ardi.afarensis.exception

class UnauthorizedException(
    message: String,
    cause: Throwable? = null
) : RuntimeException() {
    constructor(message: String) : this(message, null)
    constructor(cause: Throwable) : this("Unauthorized", cause)
}