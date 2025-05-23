package com.ardi.afarensis.controller

import com.ardi.afarensis.service.SystemSettingService
import kotlinx.coroutines.supervisorScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/public/system-setting")
class PublicSystemSettingController(
    private val systemSettingService: SystemSettingService
) {

    @GetMapping("")
    suspend fun get() = supervisorScope {
        systemSettingService.findAllByPublic(true)
    }

}