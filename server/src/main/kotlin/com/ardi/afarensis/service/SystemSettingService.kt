package com.ardi.afarensis.service

import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.SystemSettingDto
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.request.RequestSystemSetting
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.provider.MailProvider
import com.ardi.afarensis.repository.SystemSettingRepository
import kotlinx.coroutines.supervisorScope
import org.springframework.cloud.context.scope.refresh.RefreshScope
import org.springframework.stereotype.Service

@Service
class SystemSettingService(
    private val systemSettingRepository: SystemSettingRepository,
    private val systemSettings: Map<SystemSettingKey, SystemSettingDto>,
    private val refreshScope: RefreshScope,
    private val mailProvider: MailProvider,
) {

    suspend fun findByKey(key: SystemSettingKey) = supervisorScope {
        systemSettingRepository.findByKey(key)
    }

    suspend fun findAllByPublic(isPublic: Boolean) = supervisorScope {
        systemSettings.filter { it.value.public == isPublic }
    }

    suspend fun updateSwitch(req: RequestSystemSetting.General) = supervisorScope {
        when (req.key) {
            SystemSettingKey.SMTP -> updateSmtp(req.value)
            else -> update(req.key, req.value)
        }
    }
    

    suspend fun updateSmtp(value: Map<String, Any>) = supervisorScope {
        val result = update(SystemSettingKey.SMTP, value)

        if (result.data!!) {
            refreshScope.refresh("javaMailSender")
        }

        result
    }

    suspend fun update(key: SystemSettingKey, value: Map<String, Any>) = supervisorScope {
        val systemSetting = systemSettingRepository.findByKey(key)
            ?: throw IllegalArgumentException("System setting not found")

        systemSetting.value = value

        systemSettingRepository.save(systemSetting)

        refreshScope.refresh("systemSetting")

        ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "System setting updated successfully",
            data = true,
        )
    }

    suspend fun testSmtp(req: RequestSystemSetting.Smtp) = supervisorScope {
        val result = mailProvider.testSmtp(req)

        ResponseStatus(
            status = if (result) ResStatus.SUCCESS else ResStatus.ERROR,
            message = "SMTP test ${if (result) "successful" else "failed"}",
            data = result,
        )
    }

    suspend fun init(req: RequestSystemSetting.Init) = supervisorScope {

        val systemSetting = systemSettingRepository.findByKey(req.key)
            ?: throw IllegalArgumentException("System setting not found")

        systemSetting.value = systemSetting.initValue;

        systemSettingRepository.save(systemSetting)

        refreshScope.refresh("systemSetting")

        ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "System setting initialized successfully",
            data = true,
        )
    }
}