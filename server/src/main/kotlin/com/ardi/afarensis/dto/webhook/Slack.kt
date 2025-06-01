package com.ardi.afarensis.dto.webhook

class Slack {
    data class Message(
        val text: String? = null,
        val channel: String? = null,
        val username: String? = null,
        val iconEmoji: String? = null,
        val iconUrl: String? = null,
        val attachments: List<Attachment>? = null,
    )

    data class Attachment(
        val fallback: String? = null,
        val color: String? = null, // 색상 (예: "#36a64f")
        val pretext: String? = null,
        val author_name: String? = null,
        val title: String? = null,
        val titleLink: String? = null,
        val text: String? = null,
        val fields: List<Field>? = null,
        val image_url: String? = null,
        val thumb_url: String? = null
    )

    data class Field(
        val title: String? = null,
        val value: String? = null,
        val short: Boolean? = null
    )


}