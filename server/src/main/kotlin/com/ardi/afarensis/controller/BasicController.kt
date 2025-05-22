package com.ardi.afarensis.controller

import kotlinx.coroutines.supervisorScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/public/basic")
class BasicController {

    @GetMapping("/test")
    suspend fun test() = supervisorScope {
        "test"
    }


}