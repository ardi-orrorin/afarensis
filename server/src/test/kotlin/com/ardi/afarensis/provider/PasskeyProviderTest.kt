package com.ardi.afarensis.provider


import com.yubico.webauthn.RelyingParty
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PasskeyProviderTest {

    @MockitoSpyBean
    lateinit var passkeyProvider: PasskeyProvider

    @MockitoSpyBean
    lateinit var relyingParty: RelyingParty

    val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Test
    fun createUserIdentity() {
        val userIdentity = passkeyProvider.createUserIdentity("test")

        assertTrue {
            userIdentity.name == "test"
        }

        assertFalse {
            userIdentity.displayName == "test"
        }

        assertFalse {
            userIdentity.id.bytes.contentEquals(ByteArray(64))
        }
    }

    @Test
    fun createCredentialId() {
        val userIdentity = passkeyProvider.createUserIdentity("master")
        val credentialId = passkeyProvider.createCredentialCreationOptions(userIdentity)

        val credentialIdJson = passkeyProvider.createCredentialCreationOptions(userIdentity).toJson()


        log.info("credentialId: ${credentialId.toJson()}")
    }

    @Test
    fun registrationResult() {
    }

    @Test
    fun createAssertionRequest() {
    }

    @Test
    fun assertionResult() {
    }
}