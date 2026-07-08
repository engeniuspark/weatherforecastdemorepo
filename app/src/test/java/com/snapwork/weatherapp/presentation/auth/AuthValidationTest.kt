package com.snapwork.weatherappdemo.presentation.auth

import com.snapwork.weatherappdemo.utils.HashUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AuthValidationTest {

    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    private fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && emailRegex.matches(email)
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    @Test
    fun testEmailValidation() {
        assertTrue(validateEmail("user@example.com"))
        assertTrue(validateEmail("user.name+label@example.co.uk"))
        assertFalse(validateEmail("plainaddress"))
        assertFalse(validateEmail("@missingusername.com"))
        assertFalse(validateEmail("username@.com"))
        assertFalse(validateEmail(""))
    }

    @Test
    fun testPasswordValidation() {
        assertTrue(validatePassword("123456"))
        assertTrue(validatePassword("password123"))
        assertFalse(validatePassword("12345"))
        assertFalse(validatePassword(""))
    }

    @Test
    fun testPasswordHashing() {
        val rawPassword = "mySecurePassword123"
        val hash1 = HashUtils.sha256(rawPassword)
        val hash2 = HashUtils.sha256(rawPassword)

        assertEquals(64, hash1.length)
        assertEquals(hash1, hash2)
        
        val differentHash = HashUtils.sha256("differentPassword")
        assertFalse(hash1 == differentHash)
    }
}
