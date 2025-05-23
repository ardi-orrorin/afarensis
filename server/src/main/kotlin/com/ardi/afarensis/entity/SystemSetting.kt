package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.entity.converter.JsonToMapConverter
import jakarta.persistence.*

@Entity
@Table(name = "system_settings")
class SystemSetting(
    @Id
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    var key: SystemSettingKey,

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonToMapConverter::class)
    var value: Map<String, Any> = mutableMapOf(),

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonToMapConverter::class)
    var initValue: Map<String, Any> = mutableMapOf(),
    var public: Boolean = true,
) {
    final fun toDto() = SystemSettingDto(
        key = key,
        value = value,
        initValue = initValue,
        public = public,
    )

}