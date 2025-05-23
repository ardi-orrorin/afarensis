package com.ardi.afarensis.entity.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class JsonToMapConverter : AttributeConverter<Map<String, Any>, String> {

    private val objMapper = ObjectMapper().registerModules(KotlinModule.Builder().build())

    override fun convertToDatabaseColumn(attribute: Map<String, Any>?): String {
        return attribute?.let {
            try {
                objMapper.writeValueAsString(it)
            } catch (e: Exception) {
                throw RuntimeException("JSON writing error", e)
            }
        } ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, Any> {
        return dbData?.let {
            try {
                objMapper.readValue(it, object : TypeReference<Map<String, Any>>() {})
            } catch (e: Exception) {
                throw RuntimeException("JSON reading error", e)
            }
        } ?: emptyMap()
    }
}