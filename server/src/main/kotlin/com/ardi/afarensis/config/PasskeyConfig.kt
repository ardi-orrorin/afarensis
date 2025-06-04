package com.ardi.afarensis.config

import com.ardi.afarensis.cache.CacheSystemSetting
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.repository.CredentialRepositoryImpl
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.RelyingPartyIdentity
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PasskeyConfig(
    private val credentialRepositoryImpl: CredentialRepositoryImpl,
    private val cacheSystemSetting: CacheSystemSetting,
) {

    @Bean
    fun relyingParty(relyingPartyIdentity: RelyingPartyIdentity?): RelyingParty? {
        val sysPasskey = cacheSystemSetting.getSystemSetting()[SystemSettingKey.PASSKEY]?.value
            ?: throw Exception("Passkey setting not found")

        val domain: String = sysPasskey["domain"] as String
        val port: Int = sysPasskey["port"] as Int

        return RelyingParty.builder()
            .identity(relyingPartyIdentity)
            .credentialRepository(credentialRepositoryImpl)
            .origins(setOf("https://$domain:$port"))
            .build()
    }


    @Bean
    fun relyingPartyIdentity(): RelyingPartyIdentity {
        val sysPasskey = cacheSystemSetting.getSystemSetting()[SystemSettingKey.PASSKEY]?.value
            ?: throw Exception("Passkey setting not found")

        val domain: String = sysPasskey["domain"] as String

        val displayName: String = sysPasskey["displayName"] as String

        return RelyingPartyIdentity.builder()
            .id(domain)
            .name(displayName)
            .build()
    }
}