package com.ardi.afarensis.service

import com.ardi.afarensis.dto.OtpStatus
import com.ardi.afarensis.dto.ResStatus
import com.ardi.afarensis.dto.SystemSettingKey
import com.ardi.afarensis.dto.response.ResponseStatus
import com.ardi.afarensis.provider.OtpProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OtpService(
    private val otpProvider: OtpProvider
) : BasicService() {

    fun generateOtp(userPk: String): ResponseStatus<String> {
        val user = userRepository.findById(userPk).orElseThrow {
            throw IllegalArgumentException("User not found")
        }

        val userDto = user.toDto()

        val issuer = systemSetting.getSystemSetting()[SystemSettingKey.OTP]
            ?.value?.get("issuer") as String?
            ?: throw IllegalArgumentException("OTP issuer not found")


        val secretKey = otpProvider.generateOtpSecret()

        user.otp = null
        userRepository.save(user)
        userRepository.flush()

        user.addOtp(secretKey)

        userRepository.save(user)

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "OTP generated successfully",
            data = otpProvider.generateOtpQrcode(userDto.userId, issuer, secretKey)
        )
    }

    fun verifyOtp(userPk: String, code: String): ResponseStatus<Boolean> {
        val user = userRepository.findById(userPk).orElseThrow {
            throw IllegalArgumentException("User not found")
        }

        if (user.otp == null) {
            throw IllegalArgumentException("OTP not found")
        }

        val isVerified = otpProvider.verifyOtp(user.otp!!.hash, code)

        if (!isVerified) {
            return ResponseStatus(
                status = ResStatus.FAILED,
                message = "OTP verified Failed",
                data = false
            )
        }

        user.otp!!.status = OtpStatus.COMPLETED

        userRepository.save(user)

        return ResponseStatus(
            status = ResStatus.SUCCESS,
            message = "OTP verified successfully",
            data = true
        )
    }

}