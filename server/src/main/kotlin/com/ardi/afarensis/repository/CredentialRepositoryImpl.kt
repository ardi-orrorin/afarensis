package com.ardi.afarensis.repository

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
@Transactional(readOnly = true)
class CredentialRepositoryImpl(
    val userRepository: UserRepository,
    val passkeyRepository: UserPasskeyRepository
) : CredentialRepository {

    override fun getCredentialIdsForUsername(username: String?): MutableSet<PublicKeyCredentialDescriptor> {
        if (username == null) {
            throw IllegalArgumentException("Username must not be null")
        }
        val passkeys = userRepository.findByUserId(username)?.let {
            it.userPasskeys.map { passkey -> passkey.toDto() }
        } ?: throw IllegalArgumentException("User not found")

        return passkeys.map { it.toPublicKeyCredentialDescriptor() }.toMutableSet()
    }

    override fun getUserHandleForUsername(username: String?): Optional<ByteArray> {
        if (username == null) {
            throw IllegalArgumentException("Username must not be null")
        }
        val userHandle = userRepository.findByUserId(username)?.userPasskeys?.firstOrNull()?.userHandle
            ?: throw IllegalArgumentException("User not found")

        return Optional.of(ByteArray(userHandle))
    }


    override fun getUsernameForUserHandle(userHanlde: ByteArray?): Optional<String> {
        if (userHanlde == null) {
            throw IllegalArgumentException("User Handle must not be null")
        }

        val userId = passkeyRepository.findFirstByUserHandle(userHanlde.bytes)?.user?.userId
            ?: throw IllegalArgumentException("Passkey not found")

        return Optional.of(userId)
    }

    override fun lookup(credentialId: ByteArray?, userHandle: ByteArray?): Optional<RegisteredCredential> {
        if (credentialId == null || userHandle == null) {
            throw IllegalArgumentException("Credential ID and User Handle must not be null")
        }

        val passkeyDto = passkeyRepository.findByCredentialAndUserHandle(credentialId.bytes, userHandle.bytes)
            ?.toDto()
            ?: throw IllegalArgumentException("Passkey not found")

        return Optional.of(passkeyDto.toRegisteredCredential())
    }

    override fun lookupAll(credentialId: ByteArray?): MutableSet<RegisteredCredential> {
        if (credentialId == null) {
            throw IllegalArgumentException("Credential ID must not be null")
        }

        val passkeys = passkeyRepository.findAllByCredential(credentialId.bytes).map {
            it.toDto()
        }

        return passkeys.map { it.toRegisteredCredential() }.toMutableSet()
    }
}