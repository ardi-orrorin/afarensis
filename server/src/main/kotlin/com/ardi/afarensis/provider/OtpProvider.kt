package com.ardi.afarensis.provider

import org.apache.commons.codec.binary.Base32
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class OtpProvider(
    private val base32: Base32
) {
    fun generateOtpSecret(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return base32.encodeToString(bytes).replace("=", "")
    }

    fun generateOtpQrcode(user: String, issuer: String, secret: String): String {
        val label = URLEncoder.encode("$issuer:$user", "UTF-8")

        val issuerEnc = URLEncoder.encode(issuer, "UTF-8")
        return "otpauth://totp/$label?secret=$secret&issuer=$issuerEnc&algorithm=SHA1&digits=6&period=30"
    }

    fun verifyOtp(secret: String, code: String, time: Long = System.currentTimeMillis()): Boolean {
        val base32 = Base32()
        val key = base32.decode(secret)
        val timeWindow = time / 1000 / 30
        for (i in -1..1) {
            val otp = generateTOTP(key, timeWindow + i)
            if (otp == code) return true
        }
        return false
    }

    private fun generateTOTP(key: ByteArray, timeWindow: Long): String {
        val data = ByteArray(8)
        var value = timeWindow
        for (i in 7 downTo 0) {
            data[i] = (value and 0xFF).toByte()
            value = value shr 8
        }
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key, "HmacSHA1"))
        val hash = mac.doFinal(data)
        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)
        val otp = binary % 1_000_000
        return String.format("%06d", otp)
    }
}