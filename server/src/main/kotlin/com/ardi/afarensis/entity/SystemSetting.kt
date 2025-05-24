package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.entity.converter.JsonToMapConverter
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "system_settings")
class SystemSetting(
    @Id
    @Column(updatable = false)
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "jsonb", nullable = false, updatable = false)
    var key: SystemSettingKey,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Convert(converter = JsonToMapConverter::class)
    var value: Map<String, Any> = mutableMapOf(),

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false, updatable = false)
    @Convert(converter = JsonToMapConverter::class)
    var initValue: Map<String, Any> = mutableMapOf(),

    @Column(updatable = false)
    var public: Boolean = true,
) {
    final fun toDto() = SystemSettingDto(
        key = key,
        value = value,
        initValue = initValue,
        public = public,
    )

}