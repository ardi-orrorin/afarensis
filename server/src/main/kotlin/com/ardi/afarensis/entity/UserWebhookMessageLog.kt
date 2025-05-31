package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.UserWebhookMessageLogDto
import com.ardi.afarensis.entity.converter.JsonToMapConverter
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
@Table(name = "users_webhooks_message_logs")
class UserWebhookMessageLog(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Convert(converter = JsonToMapConverter::class)
    var message: Map<String, Any> = mapOf(),

    var createdAt: Instant = Instant.now(),

    @ManyToOne
    @JoinColumn(name = "users_pk", insertable = true, updatable = true, nullable = false)
    var user: User,

    @ManyToOne
    @JoinColumn(name = "users_webhooks_pk", insertable = true, updatable = true, nullable = false)
    var userWebhooks: UserWebhook,
) {

    fun toDto() = UserWebhookMessageLogDto(
        id,
        user.id ?: "",
        userWebhooks.id ?: 0,
        message,
        createdAt,
    )
}