package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
class PasskeyServiceTest {

    @MockitoSpyBean
    lateinit var passkeyService: PasskeyService

    val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Test
    fun createCredentials() {
        assertThrows<IllegalArgumentException> {
            passkeyService.createCredentialOptions("test")
        }

        val credential = passkeyService.createCredentialOptions("master")

        assertTrue {
            credential.status == ResStatus.SUCCESS
        }

    }


}