package com.ardi.afarensis.entity.converter

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class JsonToPublicKeyCredentialCreationOptions : AttributeConverter<PublicKeyCredentialCreationOptions, String> {

    override fun convertToDatabaseColumn(attribute: PublicKeyCredentialCreationOptions?): String {
        if (attribute == null) {
            throw IllegalArgumentException("PublicKeyCredentialCreationOptions cannot be null")
        }

        return attribute.toJson()
    }

    override fun convertToEntityAttribute(dbData: String?): PublicKeyCredentialCreationOptions {
        if (dbData == null) {
            throw IllegalArgumentException("PublicKeyCredentialCreationOptions cannot be null")
        }

        return PublicKeyCredentialCreationOptions.fromJson(dbData)
    }
}
