package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.Role
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.dto.response.ResponseUser
import com.ardi.afarensis.exception.UnSignRefreshTokenException
import com.ardi.afarensis.exception.UnauthorizedException
import com.ardi.afarensis.provider.TokenProvider
import com.ardi.afarensis.util.StringUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.mono
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
    private val systemSettingService: SystemSettingService
) : ReactiveUserDetailsService, BasicService() {

    override fun findByUsername(username: String?): Mono<UserDetails> = mono {
        val user = userRepository.findByUserId(username!!)
            ?: throw IllegalArgumentException("User not found")

        user.toUserDetailDto()
    }

    suspend fun findByUserId(userId: String) = withContext(Dispatchers.IO) {
        userRepository.findByUserId(userId)?.toDto()
            ?: throw IllegalArgumentException("User not found")
    }


    @Transactional(readOnly = true)
    suspend fun findAll() = supervisorScope {
        val users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "userId"))

        if (users.isEmpty()) {
            return@supervisorScope listOf()
        }

        users.map { it.toDto() }
    }

    suspend fun save(req: RequestUser.SignUp) = withContext(Dispatchers.IO) {
        if (userRepository.existsByUserId(req.userId)) {
            throw IllegalArgumentException("User already exists")
        }

        req.toEntity().let {
            it.pwd = bCryptPasswordEncoder.encode(it.pwd)
            it.addRole(Role.USER)
            userRepository.save(it)
        }.toDto()
    }

    @Transactional
    suspend fun signIn(req: RequestUser.SignIn, ip: String, userAgent: String) = withContext(Dispatchers.IO) {
        val user = userRepository.findByUserId(req.userId) ?: throw RuntimeException("User not found")

        val userDto = user.toDto();

        if (!bCryptPasswordEncoder.matches(req.pwd, userDto.pwd)) {
            throw IllegalArgumentException("Password not matched")
        }

        if (userDto.userRefreshToken != null) {
            user.removeRefreshToken()
            userRepository.save(user)
        }

        val accessToken = async {
            tokenProvider.generateToken(userDto.userId, false)
        }

        val refreshToken = async {
            tokenProvider.generateToken(userDto.userId, true)
        }

        user.addRefreshToken(
            refreshToken.await(),
            ip,
            userAgent,
            Instant.now().plus(tokenProvider.REFRESH_EXP, ChronoUnit.SECONDS)
        )

        userRepository.save(user)

        ResponseUser.SignIn(
            accessToken.await(),
            tokenProvider.ACCESS_EXP,
            refreshToken.await(),
            tokenProvider.REFRESH_EXP,
            userDto.userId,
            userDto.roles.toSet(),
        )
    }

    @Transactional
    suspend fun publishAccessToken(req: RequestUser.RefreshToken) = withContext(Dispatchers.IO) {
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

        ResponseUser.SignIn(
            accessToken,
            tokenProvider.ACCESS_EXP,
            "",
            tokenProvider.REFRESH_EXP,
            user.userId,
            user.userRoles.map { it.role }.toSet(),
        )
    }

    @Transactional
    suspend fun signOut(userId: String) = withContext(Dispatchers.IO) {
        val user = userRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("User not found")

        user.removeRefreshToken()
        userRepository.save(user)

        ResponseStatus(
            ResStatus.SUCCESS,
            "Sign out success",
            true,
        )
    }

    @Transactional
    suspend fun sendVerifyCode(req: RequestUser.ResetPassword) = withContext(Dispatchers.IO) {
        val user = userRepository.findByUserId(req.userId)
            ?: throw IllegalArgumentException("User not found")

        if (user.email != req.email) {
            throw IllegalArgumentException("Email not matched")
        }

        val verifyCode = stringUtil.generateStr(6)

        // TODO: send email
    }


    @Transactional
    suspend fun resetPassword(req: RequestUser.ResetPassword) = withContext(Dispatchers.IO) {
        if (req.code.isNullOrEmpty()) {
            throw IllegalArgumentException("Invalid verification code")
        }

        val user = userRepository.findByUserId(req.userId)
            ?: throw RuntimeException("User not found")


        val verifyEmail = user.userVerifyEmails.find { it.verifyKey == req.code }
            ?: throw IllegalArgumentException("Invalid verification code")
        if (verifyEmail.expiredAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Email verification token expired")
        }

        val newPwd = bCryptPasswordEncoder.encode(stringUtil.generateStr(10))
        user.pwd = newPwd
        userRepository.save(user)

        ResponseStatus(
            ResStatus.SUCCESS,
            "Password reset success",
            true,
        )
    }

    @Transactional
    fun updateMaster(req: RequestUser.InitMasterUpdate, role: Role) = runBlocking {
        val sysInit = getCacheSystemSettingKey(SystemSettingKey.INIT)
        val sysInitValue = sysInit?.value ?: throw RuntimeException("System not Init value")
        val initialized = sysInitValue["initialized"] as Boolean
        val isUpdatedMasterPwd = sysInitValue["isUpdatedMasterPwd"] as Boolean
        if ((role == Role.GUEST || role == Role.USER || role == Role.ADMIN) && (initialized || isUpdatedMasterPwd)) {
            return@runBlocking ResponseStatus(
                ResStatus.FAILED,
                "Master password already updated",
                false,
            )
        }

        val master = userRepository.findByUserId("master")
            ?: throw RuntimeException("User not found")
        master.pwd = bCryptPasswordEncoder.encode(req.pwd)
        master.email = req.email


        systemSettingService.updateInit()

        ResponseStatus(
            ResStatus.SUCCESS,
            "Master password updated",
            true,
        )
    }
}