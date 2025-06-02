package com.ardi.afarensis.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

class RequestUser {
    data class SignIn(
        @field:NotBlank(message = "아이디를 입력해주세요.")
        val userId: String,
        @field:NotBlank(message = "비밀번호를 입력해주세요.")
        val pwd: String,
    )

    data class SignUp(
        @field:NotBlank(message = "아이디를 입력해주세요.")
        val userId: String,
        @field:NotBlank(message = "비밀번호를 입력해주세요.")
        val pwd: String,
        @field:NotBlank(message = "이메일을 입력해주세요.")
        @field:Email(message = "이메일 형식이 아닙니다.")
        val email: String,
    ) {
        fun toEntity() = com.ardi.afarensis.entity.User(
            userId = userId,
            pwd = pwd,
            email = email,
        )
    }

    data class RefreshToken(
        @field:NotBlank(message = "리프레시 토큰을 입력해주세요.")
        val refreshToken: String,
        @field:NotBlank(message = "유저 아이디를 입력해주세요.")
        val userId: String,
        val ip: String,
        val userAgent: String,
    ) {}

    data class ResetPassword(
        @field:NotBlank(message = "유저 아이디를 입력해주세요.")
        val userId: String,
        @field:Email(message = "이메일 형식이 아닙니다.")
        val email: String
    ) {}

    data class InitMasterUpdate(
        @field:NotBlank(message = "비밀번호를 입력해주세요.")
        val pwd: String,
        @field:NotBlank(message = "이메일을 입력해주세요.")
        @field:Email(message = "이메일 형식이 아닙니다.")
        val email: String,
        @field:NotBlank(message = "이메일을 입력해주세요.")
        @field:URL(message = "URL 형식이 아닙니다.")
        val homeUrl: String,
    )

    data class UpdatePassword(
        @field:NotBlank(message = "비밀번호를 입력해주세요.")
        val pwd: String,
        @field:NotBlank(message = "새로운 비밀번호를 입력해주세요.")
        val newPwd: String,
    )
}