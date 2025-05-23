package com.ardi.afarensis.service

import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SystemSettingServiceTest {

    @MockitoSpyBean
    lateinit var systemSettingService: SystemSettingService

    @MockitoSpyBean
    lateinit var systemSettings: Map<SystemSettingKey, SystemSettingDto>;

    val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Test
    fun findByKey() {
        val result = systemSettingService.findByKey(SystemSettingKey.INIT)
            ?: throw Exception("System Setting Not Found")

        log.info(result.toDto().toString())

        log.info(result.value["isUpdatedMasterPwd"].toString())
    }
    
}