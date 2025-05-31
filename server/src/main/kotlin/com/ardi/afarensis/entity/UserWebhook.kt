package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.UserWebhookDto
import com.ardi.afarensis.dto.WebhookType
import jakarta.persistence.*
import org.hibernate.annotations.DialectOverride
import org.hibernate.annotations.SQLRestriction
import org.hibernate.dialect.PostgreSQLDialect
import java.time.Instant

@Entity
@Table(name = "users_webhooks")
@DialectOverride.SQLDelete(
    override = org.hibernate.annotations.SQLDelete(sql = "UPDATE users_webhooks SET deleted_at = NOW(), is_deleted = TRUE WHERE id = ?"),
    dialect = PostgreSQLDialect::class
)
@SQLRestriction("deleted_at IS NULL AND is_deleted = FALSE")
class UserWebhook(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    var type: WebhookType,

    var url: String,

    var secret: String,

    var createdAt: Instant = Instant.now(),

    var isDeleted: Boolean = false,

    var deletedAt: Instant? = null,

    @ManyToOne
    @JoinColumn(name = "users_pk", insertable = true, updatable = true, nullable = false)
    var user: User? = null,

    @OneToMany(mappedBy = "userWebhooks", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var webhookMessageLogs: MutableList<UserWebhookMessageLog> = mutableListOf(),
) {

    fun toDto() = UserWebhookDto(
        id,
        user?.id ?: "",
        type,
        url,
        secret,
        createdAt,
        webhookMessageLogs.map { it.toDto() }.toMutableList(),
    )

    fun addWebhookMessageLog(webhookMessageLog: UserWebhookMessageLog) {
        webhookMessageLogs.add(webhookMessageLog)
        webhookMessageLog.userWebhooks = this
    }
}