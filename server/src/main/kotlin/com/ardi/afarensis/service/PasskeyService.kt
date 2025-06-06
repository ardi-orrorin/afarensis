package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.response.ResponsePasskey
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.entity.User
import com.ardi.afarensis.entity.UserPasskey
import com.ardi.afarensis.entity.UserPasskeyPendingAssertion
import com.ardi.afarensis.entity.UserPasskeyPendingRegistration
import com.ardi.afarensis.provider.PasskeyProvider
import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class PasskeyService(
    private val passkeyProvider: PasskeyProvider,
) : BasicService() {

    @Transactional(readOnly = true)
    fun findAllByUserPk(userPk: String): ResponsePasskey.SummaryList {
        val user = userRepository.findById(userPk)
            .orElseThrow {
                throw IllegalArgumentException("User not found")
            }

        return ResponsePasskey.SummaryList(
            user.userPasskeys.map {
                it.toSummary()
            }.toMutableList()
        )
    }

    fun createCredentialOptions(username: String): ResponseStatus<String> {
        val user = userRepository.findByUserId(username)
            ?: throw IllegalArgumentException("User not found")

        val userIdentity = user.userPasskeyPendingRegistrations.let {
            if (user.userPasskeys.isEmpty()) {
                passkeyProvider.createUserIdentity(username)
            } else {
                passkeyProvider.createUserIdentity(username, user.userPasskeys.first().userHandle)
            }
        }

        val credential = passkeyProvider.createCredentialCreationOptions(userIdentity)

        user.removePendingRegistration()
        user.addPendingRegistration(credential)

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Success",
            data = credential.toCredentialsCreateJson()
        )
    }

    fun finishRegistration(username: String, json: String, userAgent: String): ResponseStatus<Boolean> {
        val user = userRepository.findByUserId(username)
            ?: throw IllegalArgumentException("User not found")

        val pendingRegistration = user.userPasskeyPendingRegistrations.firstOrNull()
            ?: throw IllegalArgumentException("Pending registration not found")

        val result = passkeyProvider.registrationResult(pendingRegistration.options, json)

        val deviceName = Regex("""\(([^;]+);""").find(userAgent)?.groups?.get(1)?.value ?: "Unknown"

        user.addPasskey(
            pendingRegistration.options.user.id.bytes,
            result.keyId.id.bytes,
            result.publicKeyCose.bytes,
            deviceName
        )

        user.removePendingRegistration()
        userRepository.save(user)

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Success",
            data = true
        )
    }

    fun createAssertionRequest(username: String): ResponseStatus<String> {
        val user = userRepository.findByUserId(username)
            ?: throw IllegalArgumentException("User not found")

        if (user.userPasskeys.isEmpty()) {
            return ResponseStatus(
                status = ResStatus.SKIP,
                message = "User has no passkeys",
                data = ""
            )
        }

        val request = passkeyProvider.createAssertionRequest(username)

        user.removePendingAssertion()
        user.addPendingAssertion(request)

        userRepository.save(user)

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Success",
            data = request.toCredentialsGetJson()
        )
    }

    fun finishAssertion(username: String, json: String): ResponseStatus<Boolean> {
        val user = userRepository.findByUserId(username)
            ?: throw IllegalArgumentException("User not found")


        val requests = user.userPasskeyPendingAssertions.map {
            it.request
        }

        val result = requests.map { passkeyProvider.assertionResult(it, json) }
            .filter { it.isSuccess }.firstOrNull()

        if (result == null) {
            throw IllegalArgumentException("Assertion failed")
        }

        user.userPasskeys.find { it.credential.contentEquals(result.credential.credentialId.bytes) }?.let {
            it.lastUsedAt = Instant.now()
        }

        user.removePendingAssertion()

        userRepository.save(user)

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Success",
            data = true
        )
    }


    fun User.addPasskey(userHandle: ByteArray, credential: ByteArray, publicKey: ByteArray, deviceName: String) {
        userPasskeys.add(
            UserPasskey(
                userHandle = userHandle,
                credential = credential,
                publicKey = publicKey,
                deviceName = deviceName,
                user = this
            )
        )
    }

    fun User.updateUsedPasskey(id: String, deviceName: String) {
        userPasskeys.find { it.id == id }?.let {
            it.deviceName = deviceName
        } ?: throw IllegalArgumentException("Passkey not found")
    }

    fun User.addPendingRegistration(options: PublicKeyCredentialCreationOptions) {
        val pendingRegistration = UserPasskeyPendingRegistration(
            options = options,
            user = this
        )
        userPasskeyPendingRegistrations.add(pendingRegistration)
    }

    fun User.removePendingRegistration() {
        userPasskeyPendingRegistrations.clear()
    }

    fun User.addPendingAssertion(request: AssertionRequest) {
        val pendingAssertion = UserPasskeyPendingAssertion(
            request = request,
            user = this
        )
        userPasskeyPendingAssertions.add(pendingAssertion)
    }

    fun User.removePendingAssertion() {
        userPasskeyPendingAssertions.clear()
    }

    fun delete(userPk: String, passkeyId: String): ResponseStatus<Boolean> {
        val user = userRepository.findById(userPk).orElseThrow {
            throw IllegalArgumentException("User not found")
        }

        user.userPasskeys.removeIf { it.id == passkeyId }

        userRepository.save(user)

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "Passkey deleted successfully",
            data = true,
        )
    }
}