package com.ardi.afarensis.dto.webhook

import java.time.Instant

data class Webhook(
    val url: String,
    val content: String,
    val title: String,
    val path: String = "",
    val thumbnail: String,
    val author: String,
    val timestamp: Instant = Instant.now()
) {
    fun convertToTruncatedContent(content: String): String {
        return content.take(30) + if (content.length > 30) "..." else ""
    }

    fun toDiscord(): Discord.Message {
        val truncatedContent = convertToTruncatedContent(content)

        val embed = Discord.Embed(
            title = title,
            color = 15258703,
            url = path,
            description = truncatedContent,
            thumbnail = Discord.Thumbnail(thumbnail),
            timestamp = timestamp,
            author = Discord.Author(name = "author : $author")
        )

        val message = Discord.Message(
            username = "afarensis",
            content = title,
            embeds = listOf(embed)
        )

        return message
    }

    fun toSlack(): Slack.Message {
        val truncatedContent = convertToTruncatedContent(content)

        val field = Slack.Field(
            title = title,
            value = truncatedContent,
            short = false
        )

        val attachment = Slack.Attachment(
            pretext = "$title <$path|Link>",
            thumb_url = thumbnail,
            color = "#ff0000",
            fields = listOf(field),
            author_name = "author : $author"
        )


        val message = Slack.Message(
            username = "afarensis",
            attachments = listOf(attachment),
            iconUrl = thumbnail
        )

        return message
    }

}
