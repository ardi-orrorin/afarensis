package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.Role
import com.ardi.afarensis.dto.request.RequestUser
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.dto.response.ResponseUser
import com.ardi.afarensis.provider.TokenProvider
import com.ardi.afarensis.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
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
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val tokenProvider: TokenProvider,
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String?): Mono<UserDetails> {
        val user = userRepository.findByUserId(username!!)
            ?: return Mono.error(RuntimeException("User not found"))

        return Mono.just(user.toUserDetailDto())
    }

    suspend fun findByUserId(userId: String) = supervisorScope {
        userRepository.findByUserId(userId)?.toDto()
            ?: throw RuntimeException("User not found")
    }


    @Transactional(readOnly = true)
    suspend fun findAll() = supervisorScope {
        val users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "userId"))

        if (users.isEmpty()) {
            return@supervisorScope listOf()
        }

        users.map { it.toDto() }
    }

    suspend fun save(req: RequestUser.SignUp) = supervisorScope {
        if (userRepository.existsByUserId(req.userId)) {
            throw RuntimeException("User already exists")
        }

        req.toEntity().let {
            it.pwd = bCryptPasswordEncoder.encode(it.pwd)
            it.addRole(Role.USER)
            userRepository.save(it)
        }.toDto()
    }

    @Transactional
    suspend fun signIn(req: RequestUser.SignIn) = supervisorScope {
        val user = userRepository.findByUserId(req.userId) ?: throw RuntimeException("User not found")

        if (!bCryptPasswordEncoder.matches(req.pwd, user.pwd)) {
            throw RuntimeException("Password not matched")
        }

        if (user.userRefreshToken != null) {
            user.removeRefreshToken()
            userRepository.save(user)
        }

        val userDto = user.toDto();

        val accessToken = async {
            tokenProvider.generateToken(userDto.userId, false)
        };
        val refreshToken = async {
            tokenProvider.generateToken(userDto.userId, true)
        };
        

        user.addRefreshToken(refreshToken.await(), Instant.now().plus(tokenProvider.REFRESH_EXP, ChronoUnit.SECONDS))

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
    suspend fun publishAccessToken(req: RequestUser.RefreshToken) = supervisorScope {
        val user = userRepository.findByUserId(req.userId)
            ?: throw RuntimeException("User not found")

        user.userRefreshToken?.let { refreshToken ->
            if (refreshToken.refreshToken != req.refreshToken) {
                throw RuntimeException("Refresh token not matched")
            }

            if (refreshToken.expiredAt.isBefore(Instant.now())) {
                throw RuntimeException("Refresh token expired")
            }
        } ?: throw RuntimeException("Refresh token not found")

        val accessToken = tokenProvider.generateToken(user.userId, false)

        ResponseUser.SignIn(
            accessToken,
            tokenProvider.ACCESS_EXP,
            "",
            0L,
            user.userId,
            setOf()
        )
    }

    @Transactional
    suspend fun signOut(userId: String) = supervisorScope {
        val user = userRepository.findByUserId(userId)
            ?: throw RuntimeException("User not found")

        user.removeRefreshToken()
        userRepository.save(user)

        ResponseStatus(
            ResStatus.SUCCESS,
            "Sign out success",
            true,
        )
    }
}