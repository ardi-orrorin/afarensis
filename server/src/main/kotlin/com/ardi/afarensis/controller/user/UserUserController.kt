package com.ardi.afarensis.controller.user

import com.ardi.afarensis.service.UserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/user")
class UserUserController(
    private val userService: UserService,
) {


}