package com.ardi.afarensis.service

import com.ardi.afarensis.dto.*
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.dto.response.ResponseUser
import com.ardi.afarensis.entity.User
import com.ardi.afarensis.entity.UserRefreshToken
import com.ardi.afarensis.exception.UnauthorizedException
import com.ardi.afarensis.provider.MailProvider
import com.ardi.afarensis.provider.TokenProvider
import com.ardi.afarensis.repository.UserRefreshTokenRepository
import com.ardi.afarensis.util.StringUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
    private val webhookService: WebhookService, private val userRefreshTokenRepository: UserRefreshTokenRepository
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

    fun signIn(username: String, ip: String, userAgent: String): ResponseUser.SignIn {
        val user = userRepository.findByUserId(username)
            ?: throw RuntimeException("User not found")

        return signIn(user, ip, userAgent)
    }

    fun signIn(req: RequestUser.SignIn, ip: String, userAgent: String): ResponseUser.SignIn {
        val user = userRepository.findByUserId(req.userId)
            ?: throw RuntimeException("User not found")

        if (!bCryptPasswordEncoder.matches(req.pwd, user.pwd)) {
            throw IllegalArgumentException("Password not matched")
        }

        return signIn(user, ip, userAgent)
    }

    fun signIn(user: User, ip: String, userAgent: String): ResponseUser.SignIn {
        user.let {
            if (user.userRefreshToken != null) {
                it.removeRefreshToken()
                userRepository.save(it)
                userRepository.flush()
            }
        }

        val accessToken = tokenProvider.generateToken(user.userId, false)

        val refreshToken = tokenProvider.generateToken(user.userId, true)

        user.let {
            it.addRefreshToken(
                refreshToken,
                ip,
                userAgent,
                Instant.now().plus(tokenProvider.REFRESH_EXP, ChronoUnit.SECONDS)
            )

            userRepository.save(it)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val homeUrl = (getCacheSystemSettingKey(SystemSettingKey.INIT)?.value?.get("homeUrl") ?: "") as String
                webhookService.sendWebhookMessageByCoverageWithRoles(
                    Coverage.SIGNIN,
                    user,
                    "${user.userId}님이 로그인 했습니다.",
                    "${user.userId}님이 로그인 했습니다.",
                    homeUrl
                )
            } catch (e: Exception) {
                cancel("webhhook error", e)
            }
        }

        return ResponseUser.SignIn(
            accessToken,
            tokenProvider.ACCESS_EXP,
            refreshToken,
            tokenProvider.REFRESH_EXP,
            user.userId,
            user.userRoles.map { it.role }.toSet(),
        )
    }

    fun publishAccessToken(req: RequestUser.RefreshToken): ResponseUser.SignIn {
        val user = userRepository.findByUserId(req.userId)
            ?: throw UnauthorizedException("User not found")

        user.userRefreshToken?.let { refreshToken ->
            if (refreshToken.refreshToken != req.refreshToken) {
                throw UnauthorizedException("Refresh token not matched")
            }

            if (refreshToken.expiredAt.isBefore(Instant.now())) {
                throw UnauthorizedException("Refresh token expired")
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

        userRepository.save(user)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                mailProvider.sendMail(req.email, "Reset Password", "Your new password is $newPwd")
                val homUrl = (getCacheSystemSettingKey(SystemSettingKey.INIT)?.value?.get("homeUrl") ?: "") as String
                webhookService.sendWebhookMessageByCoverageWithRoles(
                    Coverage.PASSWORD,
                    user,
                    "Password reset success",
                    "Password reset success ${user.userId}",
                    homUrl
                )
            } catch (e: Exception) {
                cancel("Mail send failed", e)
            }
        }

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

        CoroutineScope(Dispatchers.IO).launch {
            val homUrl = (getCacheSystemSettingKey(SystemSettingKey.INIT)?.value?.get("homeUrl") ?: "") as String
            webhookService.sendWebhookMessageByCoverageWithRoles(
                Coverage.PASSWORD,
                user,
                "Changed Password success",
                "Changed Password success ${user.userId}",
                homUrl
            )
        }

        return ResponseStatus(
            ResStatus.SUCCESS,
            "Password updated",
            true,
        )
    }

    fun User.addRefreshToken(refreshToken: String, ip: String, agent: String, expiredAt: Instant) {
        userRefreshToken =
            UserRefreshToken(
                refreshToken = refreshToken,
                expiredAt = expiredAt,
                ip = ip,
                userAgent = agent,
                user = this
            )
    }

    fun User.removeRefreshToken() {
        userRefreshToken = null
    }

}