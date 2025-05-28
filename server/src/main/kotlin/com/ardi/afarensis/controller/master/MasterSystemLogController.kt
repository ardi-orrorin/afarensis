package com.ardi.afarensis.controller.master

import com.ardi.afarensis.dto.request.RequestPage
import com.ardi.afarensis.dto.request.RequestSystemLog
import com.ardi.afarensis.service.SystemLogService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/private/master/system-log")
class MasterSystemLogController(
    private val systemLogService: SystemLogService
) {

    @GetMapping("")
    suspend fun findAll(
        page: RequestPage
    ) = systemLogService.findAll(page)


    @PatchMapping("")
    suspend fun isReadById(@RequestBody req: RequestSystemLog.IsRead) =
        systemLogService.updateIsReadById(req.id)
}