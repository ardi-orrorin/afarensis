package com.ardi.afarensis.controller

import com.ardi.afarensis.service.SystemSettingService
import com.ardi.afarensis.service.UserService
import org.springframework.beans.factory.annotation.Autowired

open class BasicController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var cacheSystemSettingService: SystemSettingService
}