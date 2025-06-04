package com.ardi.afarensis.entity.converter

import com.yubico.webauthn.AssertionRequest
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class JsonToPublicAssertionRequest : AttributeConverter<AssertionRequest, String> {
    override fun convertToDatabaseColumn(attribute: AssertionRequest?): String {
        if (attribute == null) {
            throw IllegalArgumentException("AssertionRequest cannot be null")
        }

        return attribute.toJson()
    }

    override fun convertToEntityAttribute(dbData: String?): AssertionRequest {
        if (dbData == null) {
            throw IllegalArgumentException("AssertionRequest cannot be null")
        }

        return AssertionRequest.fromJson(dbData)
    }
}
