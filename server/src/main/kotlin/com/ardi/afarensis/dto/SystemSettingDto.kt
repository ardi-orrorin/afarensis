package com.ardi.afarensis.dto

data class SystemSettingDto(
    val key: SystemSettingKey,
    val value: Map<String, Any>,
    val initValue: Map<String, Any>,
    val public: Boolean,
) {}