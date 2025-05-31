package com.ardi.afarensis.service

import com.ardi.afarensis.dto.SystemSettingKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
class SystemSettingServiceTest {

    @MockitoSpyBean
    lateinit var systemSettingService: SystemSettingService


    val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Test
    fun findByKey() = runTest {
        val result = systemSettingService.findByKey(SystemSettingKey.INIT)
            ?: throw Exception("SystemSettingService.findByKey() failed")
        log.info(result.toString())

        log.info(result.value["isUpdatedMasterPwd"].toString())
    }

}