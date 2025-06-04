package com.ardi.afarensis.dto

import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import java.time.Instant

data class UserPasskeyDto(
    val id: String,
    val userHandle: ByteArray,
    val credential: ByteArray,
    val publicKey: ByteArray,
    val deviceName: String,
    val lastUsedAt: Instant = Instant.now(),
    val createdAt: Instant = Instant.now(),
) {
    fun toPublicKeyCredentialDescriptor() = PublicKeyCredentialDescriptor.builder()
        .id(com.yubico.webauthn.data.ByteArray(credential))
        .build()

    fun toRegisteredCredential(): RegisteredCredential {
        val res = RegisteredCredential.builder()
            .credentialId(com.yubico.webauthn.data.ByteArray(credential))
            .userHandle(com.yubico.webauthn.data.ByteArray(userHandle))
            .publicKeyCose(com.yubico.webauthn.data.ByteArray(publicKey))
            .signatureCount(0)
            .build()

        return res
    }
}
