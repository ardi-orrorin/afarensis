package com.ardi.afarensis.dto.webhook

import java.time.Instant

class Discord {
    data class Message(
        val username: String? = null,
        val avatar_url: String? = null,
        val content: String? = null,
        val tts: Boolean? = null,
        val embeds: List<Embed>? = null
    )

    data class Embed(
        val title: String? = null,
        val description: String? = null,
        val url: String? = null,
        val color: Int? = null,
        val author: Author? = null,
        val footer: Footer? = null,
        val thumbnail: Thumbnail? = null,
        val image: Image? = null,
        val fields: List<Field>? = null,
        val timestamp: Instant? = null
    )

    data class Author(
        val name: String? = null,
        val url: String? = null,
        val icon_url: String? = null
    )

    data class Footer(
        val text: String? = null,
        val icon_url: String? = null
    )

    data class Thumbnail(
        val url: String? = null
    )

    data class Image(
        val url: String? = null
    )

    data class Field(
        val name: String? = null,
        val value: String? = null,
        val inline: Boolean? = null
    )
}