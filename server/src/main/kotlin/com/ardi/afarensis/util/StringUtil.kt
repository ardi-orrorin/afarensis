package com.ardi.afarensis.util

import org.springframework.stereotype.Component

@Component
class StringUtil(
    private val CHARS: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()",
) {

    final fun generateStr(length: Int): String {
        val password = StringBuilder(length)
        for (i in 0 until length) {
            password.append(CHARS.random())
        }
        return password.toString()
    }


}