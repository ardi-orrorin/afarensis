package com.ardi.afarensis.controller.master

import com.ardi.afarensis.dto.request.RequestSystemSetting
import com.ardi.afarensis.service.SystemSettingService
import jakarta.validation.Valid
import kotlinx.coroutines.supervisorScope
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/master/system-setting")
class MasterSystemSettingController(
    private val systemSettingService: SystemSettingService
) {

    @GetMapping("")
    suspend fun get() = supervisorScope {
        systemSettingService.findAllByPublic(false)
    }

    @PutMapping("")
    suspend fun update(@Valid @RequestBody req: RequestSystemSetting.General) = supervisorScope {
        systemSettingService.updateRouter(req)
    }

    @PutMapping("init")
    suspend fun init(@RequestBody req: RequestSystemSetting.Init) = supervisorScope {
        systemSettingService.initRouter(req)
    }


    @PostMapping("/smtp/test")
    suspend fun testSmtp(@Valid @RequestBody req: RequestSystemSetting.Smtp) = supervisorScope {
        systemSettingService.testSmtp(req)
    }

}