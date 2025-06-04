package com.ardi.afarensis.service


import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.WebhookType
import com.ardi.afarensis.dto.request.RequestPage
import com.ardi.afarensis.dto.request.RequestWebhook
import com.github.f4b6a3.ulid.UlidCreator
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
class WebhookServiceTest {

    @MockitoSpyBean
    lateinit var webhookService: WebhookService

    @Value("\${webhook.discord}")
    lateinit var discord: String

    @Value("\${webhook.slack}")
    lateinit var slack: String


    val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Test
    fun findMessageLogByUserPk() = runTest {
        val req = RequestPage(
            page = 1,
            size = 10,
            "createdAt",
            Sort.Direction.DESC.toString(),
            ""
        )

        val res = webhookService.findMessageLogByUserPk("0000000000000000000000000", req.toPageRequest())
        log.info(res.toString())
    }

    @Test
    fun saveWebhook() = runTest {
        val req = RequestWebhook.SaveWebhook(
            id = null,
            type = WebhookType.DISCORD,
            url = "https://discord.com/api/webhooks/1234567890/abcdefghijklmnopqrstuvwxyz"
        )

        assertThrows<IllegalArgumentException> {
            val invalidUserPk = UlidCreator.getUlid().toString()
            webhookService.saveWebhook(invalidUserPk, req)
        }

        val userPk = "0000000000000000000000000"
        val res = webhookService.saveWebhook(userPk, req)

        assertTrue {
            res.data == true
        }

        assertTrue {
            res.status == ResStatus.SUCCESS
        }
    }

    @Test
    fun deleteWebhook() = runTest {
        val userPk = "0000000000000000000000000"

        assertThrows<IllegalArgumentException> {
            webhookService.deleteWebhook(userPk, 35)
        }
    }

//    deleted method
//    @Test
//    fun sendWebhookMessage() = runTest {
//        val webhook = Webhook(
//            url = discord,
//            content = "test",
//            title = "title test",
//            path = "https://www.google.com",
//            thumbnail = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
//            author = "author test"
//        )
//
//        webhookService.sendWebhookMessage(webhook)
//    }
}