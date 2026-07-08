package com.snapwork.weatherappdemo.data.repository

import com.snapwork.weatherappdemo.data.local.dao.UserDao
import com.snapwork.weatherappdemo.data.local.datastore.SessionManager
import com.snapwork.weatherappdemo.data.local.entity.UserEntity
import com.snapwork.weatherappdemo.utils.HashUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthRepositoryTest {

    private lateinit var userDao: UserDao
    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepositoryImpl

    @BeforeEach
    fun setUp() {
        userDao = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        authRepository = AuthRepositoryImpl(userDao, sessionManager)
    }

    @Test
    fun `register success stores user and saves session`() = runTest {
        val email = "new@example.com"
        val password = "password"
        
        coEvery { userDao.getUserByEmail(email) } returns null
        coEvery { userDao.insertUser(any()) } returns 1L
        coEvery { sessionManager.saveSession(email) } returns Unit

        val result = authRepository.register(email, password)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { userDao.insertUser(match { it.email == email && it.hashedPassword == HashUtils.sha256(password) }) }
        coVerify(exactly = 1) { sessionManager.saveSession(email) }
    }

    @Test
    fun `register fail duplicate email returns error`() = runTest {
        val email = "existing@example.com"
        val password = "password"
        val existingUser = UserEntity(1L, email, "hashed")

        coEvery { userDao.getUserByEmail(email) } returns existingUser

        val result = authRepository.register(email, password)

        assertTrue(result.isFailure)
        assertEquals("Email already exists", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun `login success validates password and saves session`() = runTest {
        val email = "user@example.com"
        val password = "password"
        val hashedPassword = HashUtils.sha256(password)
        val user = UserEntity(1L, email, hashedPassword)

        coEvery { userDao.getUserByEmail(email) } returns user
        coEvery { sessionManager.saveSession(email) } returns Unit

        val result = authRepository.login(email, password)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { sessionManager.saveSession(email) }
    }

    @Test
    fun `login fail wrong password returns error`() = runTest {
        val email = "user@example.com"
        val password = "password"
        val wrongPasswordHash = HashUtils.sha256("wrong_password")
        val user = UserEntity(1L, email, wrongPasswordHash)

        coEvery { userDao.getUserByEmail(email) } returns user

        val result = authRepository.login(email, password)

        assertTrue(result.isFailure)
        assertEquals("Invalid email or password", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun `login fail user not found returns error`() = runTest {
        val email = "notfound@example.com"
        coEvery { userDao.getUserByEmail(email) } returns null

        val result = authRepository.login(email, "password")

        assertTrue(result.isFailure)
        assertEquals("Invalid email or password", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getSession retrieves email flow`() = runTest {
        val expectedEmail = "user@example.com"
        every { sessionManager.userSessionFlow } returns flowOf(expectedEmail)

        val flow = authRepository.getSession()
        
        flow.collect { email ->
            assertEquals(expectedEmail, email)
        }
    }
}
