package com.ardi.afarensis.provider

import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.SystemSettingKey
import com.yubico.webauthn.*
import com.yubico.webauthn.data.PublicKeyCredential
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import com.yubico.webauthn.data.UserIdentity
import com.yubico.webauthn.exception.AssertionFailedException
import com.yubico.webauthn.exception.RegistrationFailedException
import org.springframework.stereotype.Component
import java.io.IOException
import kotlin.random.Random

@Component
class PasskeyProvider(
    private val relyingParty: RelyingParty,
    private val random: Random,
    private val cacheSystemSetting: CacheSystemSetting
) {

    fun createUserIdentity(username: String, userHandle: ByteArray = ByteArray(64)): UserIdentity {

        val displayName = cacheSystemSetting.getSystemSetting()[SystemSettingKey.PASSKEY]?.value?.let {
            it.get("displayName") as String
        } ?: throw AssertionFailedException("Passkey not found")

        if (userHandle.contentEquals(ByteArray(64))) {
            random.nextBytes(userHandle)
        }

        return UserIdentity.builder()
            .name(username)
            .displayName(displayName)
            .id(com.yubico.webauthn.data.ByteArray(userHandle))
            .build()
    }

    fun createCredentialCreationOptions(userIdentity: UserIdentity): PublicKeyCredentialCreationOptions {
        return relyingParty.startRegistration(
            StartRegistrationOptions.builder()
                .user(userIdentity)
                .build()
        )
    }

    fun registrationResult(options: PublicKeyCredentialCreationOptions?, json: String?): RegistrationResult {
        try {
            val pkc =
                PublicKeyCredential.parseRegistrationResponseJson(json)

            val finishRegistrationOptions = FinishRegistrationOptions.builder()
                .request(options)
                .response(pkc)
                .build()

            return relyingParty.finishRegistration(finishRegistrationOptions)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: RegistrationFailedException) {
            throw RuntimeException(e)
        }
    }

    fun createAssertionRequest(username: String?): AssertionRequest {
        return relyingParty.startAssertion(
            StartAssertionOptions.builder()
                .username(username)
                .build()
        )
    }

    fun assertionResult(request: AssertionRequest?, json: String?): AssertionResult {
        try {
            val pkc =
                PublicKeyCredential.parseAssertionResponseJson(json)

            val finishAssertionOptions = FinishAssertionOptions.builder()
                .request(request)
                .response(pkc)
                .build()

            return relyingParty.finishAssertion(finishAssertionOptions)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: AssertionFailedException) {
            throw RuntimeException(e)
        }
    }
}