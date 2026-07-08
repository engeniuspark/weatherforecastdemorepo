package com.snapwork.weatherappdemo.presentation.auth

import com.snapwork.weatherappdemo.domain.usecase.GetSessionUseCase
import com.snapwork.weatherappdemo.domain.usecase.LoginUseCase
import com.snapwork.weatherappdemo.domain.usecase.LogoutUseCase
import com.snapwork.weatherappdemo.domain.usecase.RegisterUseCase
import com.snapwork.weatherappdemo.presentation.UiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var getSessionUseCase: GetSessionUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var viewModel: AuthViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        loginUseCase = mockk()
        registerUseCase = mockk()
        getSessionUseCase = mockk()
        logoutUseCase = mockk()

        every { getSessionUseCase() } returns flowOf(null)

        viewModel = AuthViewModel(loginUseCase, registerUseCase, getSessionUseCase, logoutUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with invalid email sets error state`() = runTest {
        viewModel.login("invalid-email", "password")

        val state = viewModel.loginState.value
        assertTrue(state is UiState.Error)
        assertEquals("Invalid email format", (state as UiState.Error).message)
    }

    @Test
    fun `login with empty password sets error state`() = runTest {
        viewModel.login("valid@example.com", "")

        val state = viewModel.loginState.value
        assertTrue(state is UiState.Error)
        assertEquals("Password cannot be empty", (state as UiState.Error).message)
    }

    @Test
    fun `login success sets success state`() = runTest {
        val email = "valid@example.com"
        val password = "password"

        coEvery { loginUseCase(email, password) } returns Result.success(Unit)

        viewModel.login(email, password)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.loginState.value
        assertTrue(state is UiState.Success)
    }

    @Test
    fun `login repository error sets error state`() = runTest {
        val email = "valid@example.com"
        val password = "password"

        coEvery { loginUseCase(email, password) } returns Result.failure(Exception("Invalid credentials"))

        viewModel.login(email, password)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.loginState.value
        assertTrue(state is UiState.Error)
        assertEquals("Invalid credentials", (state as UiState.Error).message)
    }

    @Test
    fun `register mismatch password sets error`() = runTest {
        viewModel.register("valid@example.com", "password", "mismatch")

        val state = viewModel.registerState.value
        assertTrue(state is UiState.Error)
        assertEquals("Passwords do not match", (state as UiState.Error).message)
    }

    @Test
    fun `register success sets success state`() = runTest {
        val email = "valid@example.com"
        val password = "password"

        coEvery { registerUseCase(email, password) } returns Result.success(Unit)

        viewModel.register(email, password, password)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.registerState.value
        assertTrue(state is UiState.Success)
    }
}
