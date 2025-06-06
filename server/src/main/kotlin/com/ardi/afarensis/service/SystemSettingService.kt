package com.ardi.afarensis.service

import com.ardi.afarensis.cache.CacheJavaMailSender
import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.request.RequestSystemSetting
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.entity.SystemSetting
import com.ardi.afarensis.provider.MailProvider
import com.ardi.afarensis.repository.SystemSettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SystemSettingService(
    private val systemSettingRepository: SystemSettingRepository,
    private val cacheJavaMailSender: CacheJavaMailSender,
    private val mailProvider: MailProvider,
) : BasicService() {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun findByKey(key: SystemSettingKey) = withContext(Dispatchers.IO) {
        getCacheSystemSettingKey(key)
    }

    suspend fun findAllByPublic(isPublic: Boolean) = withContext(Dispatchers.IO) {
        getCacheSystemSetting().filter { it.value.public == isPublic }
    }

    suspend fun updateRouter(req: RequestSystemSetting.General) = withContext(Dispatchers.IO) {
        when (req.key) {
            SystemSettingKey.SMTP -> updateSmtp(req.value)
            SystemSettingKey.SIGN_UP -> updateSignUp(req.value)
            else -> update(req.key, req.value)
        }
    }

    suspend fun updateInit(homeUrl: String) = withContext(Dispatchers.IO) {
        val newValue = mapOf(
            "initialized" to true,
            "isUpdatedMasterPwd" to true,
            "homeUrl" to homeUrl,
        )

        update(SystemSettingKey.INIT, newValue)
    }

    suspend fun updateSmtp(value: Map<String, Any>) = withContext(Dispatchers.IO) {
        val signup = getCacheSystemSettingKey(SystemSettingKey.SIGN_UP)
            ?: throw IllegalArgumentException("System setting not found")

        if (value["enabled"] == false && signup.value["enabled"] == true) {
            throw IllegalArgumentException("SMTP and Sign Up must be different")
        }

        val result = update(SystemSettingKey.SMTP, value)

        if (result.data!!) {
            cacheJavaMailSender.clearCache()
        }

        result
    }

    suspend fun updateSignUp(value: Map<String, Any>) = withContext(Dispatchers.IO) {
        val smtp = getCacheSystemSettingKey(SystemSettingKey.SMTP)
            ?: throw IllegalArgumentException("System setting not found")

        if (value["enabled"] == true && smtp.value["enabled"] == false) {
            throw IllegalArgumentException("SMTP and Sign Up must be different")
        }

        update(SystemSettingKey.SIGN_UP, value)
    }

    suspend fun update(key: SystemSettingKey, value: Map<String, Any>) = withContext(Dispatchers.IO) {
        val systemSetting = systemSettingRepository.findByKey(key)
            ?: throw IllegalArgumentException("System setting not found")

        systemSetting.value = value

        save(systemSetting)

        ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "System setting updated successfully",
            data = true,
        )
    }

    suspend fun testSmtp(req: RequestSystemSetting.Smtp) = withContext(Dispatchers.IO) {
        val result = mailProvider.testSmtp(req)

        ResponseStatus(
            status = if (result) ResStatus.SUCCESS else ResStatus.ERROR,
            message = "SMTP test ${if (result) "successful" else "failed"}",
            data = result,
        )
    }

    suspend fun initRouter(req: RequestSystemSetting.Init) = withContext(Dispatchers.IO) {
        when (req.key) {
            SystemSettingKey.SMTP -> smtpInit()
            else -> init(req.key)
        }
    }

    suspend fun smtpInit() = withContext(Dispatchers.IO) {
        val signup = getCacheSystemSettingKey(SystemSettingKey.SIGN_UP)
            ?: throw IllegalArgumentException("System setting not found")

        if (signup.value["enabled"] as Boolean) {
            throw IllegalArgumentException("Sign Up must be disabled")
        }

        init(SystemSettingKey.SMTP)
    }


    suspend fun init(key: SystemSettingKey) = withContext(Dispatchers.IO) {

        val systemSetting = systemSettingRepository.findByKey(key)
            ?: throw IllegalArgumentException("System setting not found")

        systemSetting.value = systemSetting.initValue;

        save(systemSetting)

        ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "System setting initialized successfully",
            data = true,
        )
    }

    private fun save(systemSetting: SystemSetting) {
        systemSettingRepository.save(systemSetting)

        super.systemSetting.clearCache()
    }
}