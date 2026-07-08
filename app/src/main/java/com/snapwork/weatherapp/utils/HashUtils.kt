package com.snapwork.weatherappdemo.utils

import java.security.MessageDigest

object HashUtils {
    /**
     * Hashes a plain text string using SHA-256 and returns its hexadecimal representation.
     */
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
