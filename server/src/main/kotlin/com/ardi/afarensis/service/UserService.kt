package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.Role
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.UserDto
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.dto.response.ResponseUser
import com.ardi.afarensis.exception.UnSignRefreshTokenException
import com.ardi.afarensis.exception.UnauthorizedException
import com.ardi.afarensis.provider.MailProvider
import com.ardi.afarensis.provider.TokenProvider
import com.ardi.afarensis.util.StringUtil
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
@Transactional
class UserService(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val stringUtil: StringUtil,
    private val mailProvider: MailProvider,
) : ReactiveUserDetailsService, BasicService() {

    override fun findByUsername(username: String?): Mono<UserDetails> {
        val user = userRepository.findByUserId(username!!)
            ?: throw IllegalArgumentException("User not found")

        return Mono.just(user.toUserDetailDto())
    }

    fun findByUserId(userId: String): UserDto {
        return userRepository.findByUserId(userId)?.toDto()
            ?: throw IllegalArgumentException("User not found")
    }

    fun existByUserId(userId: String): ResponseStatus<Boolean> {
        return userRepository.existsByUserId(userId)
            .let {
                ResponseStatus(
                    status = ResStatus.SUCCESS,
                    message = if (it) "계정이 존재 합니다" else "사용가능한 아이디 입니다.",
                    !it
                )
            }
    }


    @Transactional(readOnly = true)
    fun findAll(): List<UserDto> {
        val users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "userId"))

        if (users.isEmpty()) {
            return listOf()
        }

        return users.map { it.toDto() }
    }

    fun save(req: RequestUser.SignUp): UserDto {
        if (userRepository.existsByUserId(req.userId)) {
            throw IllegalArgumentException("User already exists")
        }

        return req.toEntity().let {
            it.pwd = bCryptPasswordEncoder.encode(it.pwd)
            it.addRole(Role.USER)
            userRepository.save(it)
        }.toDto()
    }

    fun signIn(req: RequestUser.SignIn, ip: String, userAgent: String): ResponseUser.SignIn {
        val user = userRepository.findByUserId(req.userId)
            ?: throw RuntimeException("User not found")

        val userDto = user.toDto();

        if (!bCryptPasswordEncoder.matches(req.pwd, userDto.pwd)) {
            throw IllegalArgumentException("Password not matched")
        }

        user.let {
            if (userDto.userRefreshToken != null) {
                it.removeRefreshToken()
                userRepository.save(it)
            }
        }

        val accessToken = tokenProvider.generateToken(userDto.userId, false)

        val refreshToken = tokenProvider.generateToken(userDto.userId, true)


        user.let {
            it.addRefreshToken(
                refreshToken,
                ip,
                userAgent,
                Instant.now().plus(tokenProvider.REFRESH_EXP, ChronoUnit.SECONDS)
            )

            userRepository.save(it)
        }

        return ResponseUser.SignIn(
            accessToken,
            tokenProvider.ACCESS_EXP,
            refreshToken,
            tokenProvider.REFRESH_EXP,
            userDto.userId,
            userDto.roles.toSet(),
        )
    }


    fun publishAccessToken(req: RequestUser.RefreshToken): ResponseUser.SignIn {
        val user = userRepository.findByUserId(req.userId)
            ?: throw UnauthorizedException("User not found")

        user.userRefreshToken?.let { refreshToken ->
            if (refreshToken.refreshToken != req.refreshToken) {
                throw UnSignRefreshTokenException("Refresh token not matched")
            }

            if (refreshToken.expiredAt.isBefore(Instant.now())) {
                throw IllegalArgumentException("Refresh token expired")
            }

            if (refreshToken.ip != req.ip) {
                throw UnauthorizedException("IP not matched")
            }

            if (refreshToken.userAgent != req.userAgent) {
                throw UnauthorizedException("User agent not matched")
            }

        } ?: throw UnauthorizedException("Refresh token not found")

        val accessToken = tokenProvider.generateToken(user.userId, false)

        return ResponseUser.SignIn(
            accessToken,
            tokenProvider.ACCESS_EXP,
            "",
            tokenProvider.REFRESH_EXP,
            user.userId,
            user.userRoles.map { it.role }.toSet(),
        )
    }

    fun signOut(userId: String): ResponseStatus<Boolean> {
        return userRepository.findByUserId(userId)
            ?.let {
                it.removeRefreshToken()
                userRepository.save(it)

                ResponseStatus(
                    ResStatus.SUCCESS,
                    "Sign out success",
                    true,
                )
            } ?: throw IllegalArgumentException("User not found")
    }


    fun resetPassword(req: RequestUser.ResetPassword): ResponseStatus<Boolean> {
        val user = userRepository.findByUserId(req.userId)
            ?: throw IllegalArgumentException("User not found")

        if (user.email != req.email) {
            throw IllegalArgumentException("Email not matched")
        }

        val newPwd = stringUtil.generateStr(20)

        user.pwd = bCryptPasswordEncoder.encode(newPwd)

        runBlocking { mailProvider.sendMail(req.email, "Reset Password", "Your new password is $newPwd") }

        userRepository.save(user)

        return ResponseStatus(
            ResStatus.SUCCESS,
            "Password reset success",
            true,
        )
    }

    fun updateMaster(req: RequestUser.InitMasterUpdate, role: Role): ResponseStatus<Boolean> {
        val sysInit = getCacheSystemSettingKey(SystemSettingKey.INIT)
        val sysInitValue = sysInit?.value ?: throw RuntimeException("System not Init value")
        val initialized = sysInitValue["initialized"] as Boolean
        val isUpdatedMasterPwd = sysInitValue["isUpdatedMasterPwd"] as Boolean
        val confirmRoles = listOf(Role.GUEST, Role.USER, Role.ADMIN)
        if (confirmRoles.contains(role) && (initialized || isUpdatedMasterPwd)) {
            return ResponseStatus(
                ResStatus.FAILED,
                "Master password already updated",
                false,
            )
        }

        val master = userRepository.findByUserId("master")
            ?: throw RuntimeException("User not found")
        master.pwd = bCryptPasswordEncoder.encode(req.pwd)
        master.email = req.email

        userRepository.save(master)

        return ResponseStatus(
            ResStatus.SUCCESS,
            "Master password updated",
            true,
        )
    }

    fun updatePassword(id: String, req: RequestUser.UpdatePassword): ResponseStatus<Boolean> {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }

        val userDto = user.toDto()

        if (!bCryptPasswordEncoder.matches(req.pwd, userDto.pwd)) {
            throw IllegalArgumentException("Password not match")
        }

        user.pwd = bCryptPasswordEncoder.encode(req.newPwd)

        userRepository.save(user)

        return ResponseStatus(
            ResStatus.SUCCESS,
            "Password updated",
            true,
        )
    }
}