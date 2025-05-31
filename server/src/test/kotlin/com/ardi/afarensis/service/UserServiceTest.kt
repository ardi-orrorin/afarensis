package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.request.RequestUser
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
class UserServiceTest {

    @MockitoSpyBean
    lateinit var userService: UserService


    val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Test
    fun findByUserId() = runTest {
        assertThrows<IllegalArgumentException> {
            userService.findByUserId("admin")
        }

        val user = userService.findByUserId("master")

        log.info("user: {}", user)
    }

    @Test
    fun existByUserId() = runTest {
        val value = userService.existByUserId("master")

        assertTrue {
            value.status == ResStatus.SUCCESS
        }
        assertFalse {
            value.data as Boolean
        }

        log.info("value: {}", value)
    }

    @Test
    fun findAll() = runTest {
        val list = userService.findAll()

        assertFalse {
            list.isEmpty()
        }
    }

    @Test
    fun save() = runTest {
        val req = RequestUser.SignUp(
            userId = "test",
            pwd = "test",
            email = "test@test.com",
        )

        val res = userService.save(req)

        assertTrue {
            res.id.isNotEmpty()
        }

        assertTrue {
            res.userId == req.userId
        }

        assertTrue {
            res.email == req.email
        }
    }

    @Test
    fun signIn() = runTest {
        val reqSave = RequestUser.SignUp(
            userId = "test",
            pwd = "test",
            email = "test@test.com",
        )

        userService.save(reqSave)

        val req = RequestUser.SignIn(
            userId = "test",
            pwd = "test",
        )

        val res = userService.signIn(req, "127.0.0.1", "")
        assertTrue {
            res.userId == req.userId
        }

        assertTrue {
            res.accessToken.isNotEmpty()
        }

        assertTrue {
            res.refreshToken.isNotEmpty()
        }

        assertTrue {
            res.accessTokenExpiresIn > 0
        }

        assertTrue {
            res.refreshTokenExpiresIn > 0
        }
    }

    @Test
    fun publishAccessToken() = runTest {
        val reqSave = RequestUser.SignUp(
            userId = "test",
            pwd = "test",
            email = "test@test.com",
        )

        userService.save(reqSave)

        val req = RequestUser.SignIn(
            userId = "test",
            pwd = "test",
        )
        val ip = "127.0.0.1"

        val userAgent = ""

        val res = userService.signIn(req, ip, userAgent)

        val reqRefresh = RequestUser.RefreshToken(
            refreshToken = res.refreshToken,
            userId = res.userId,
            ip = ip,
            userAgent = userAgent,
        )

        val resRefresh = userService.publishAccessToken(reqRefresh)

        assertTrue {
            resRefresh.accessToken.isNotEmpty()
        }

        assertTrue {
            resRefresh.accessTokenExpiresIn > 0
        }

        assertTrue {
            resRefresh.roles.isNotEmpty()
        }

        assertTrue {
            resRefresh.refreshToken.isEmpty()
        }

        assertTrue {
            resRefresh.refreshTokenExpiresIn > 0
        }

        assertTrue {
            resRefresh.userId == req.userId
        }
    }

    @Test
    fun signOut() = runTest {

        assertThrows<IllegalArgumentException> {
            userService.signOut("admin")
        }

        val res = userService.signOut("master")
        assertTrue { res.data!! }
        assertTrue { res.status == ResStatus.SUCCESS }
    }

    @Test
    fun resetPassword() = runTest {
        assertThrows<IllegalArgumentException> {
            val req = RequestUser.ResetPassword(
                userId = "admin",
                email = "admin@admin.com"
            )

            userService.resetPassword(req)
        }

        val req = RequestUser.ResetPassword(
            userId = "admin1",
            email = "test@test.com"
        )

        val res = userService.resetPassword(req)
        assertTrue { res.data!! }

        assertTrue { res.status == ResStatus.SUCCESS }
    }

    @Test
    fun updateMaster() {
    }

    @Test
    fun updatePassword() {
    }
}