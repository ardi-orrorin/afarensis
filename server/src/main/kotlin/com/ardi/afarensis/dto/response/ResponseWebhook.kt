package com.ardi.afarensis.dto.response

import com.ardi.afarensis.dto.UserWebhookDto

class ResponseWebhook {

    data class List(
        val data: MutableList<UserWebhookDto>
    )
}