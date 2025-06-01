package com.ardi.afarensis.provider

import com.ardi.afarensis.dto.webhook.Webhook
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean

@SpringBootTest
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WebHookProviderTest {
    @MockitoSpyBean
    lateinit var webHookProvider: WebHookProvider

    val log = org.slf4j.LoggerFactory.getLogger(this::class.java)


    @Test
    fun discord() = runTest {
        val webhook = Webhook(
            url = "",
            content = "test",
            title = "title test",
            path = "https://www.google.com",
            thumbnail = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
            author = "author test"
        )

        val res = webHookProvider.discord(webhook)

        println(res)
    }

    @Test
    fun toSlack() = runTest {
        val webhook = Webhook(
            url = "",
            content = "test",
            title = "title test",
            path = "https://www.google.com",
            thumbnail = "https://a.slack-edge.com/80588/marketing/img/meta/slack_hash_75.png",
            author = "author test"
        )


        webHookProvider.slack(webhook)

    }
}