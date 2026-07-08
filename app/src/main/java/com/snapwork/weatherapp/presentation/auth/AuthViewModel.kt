package com.snapwork.weatherapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapwork.weatherapp.domain.usecase.GetSessionUseCase
import com.snapwork.weatherapp.domain.usecase.LoginUseCase
import com.snapwork.weatherapp.domain.usecase.LogoutUseCase
import com.snapwork.weatherapp.domain.usecase.RegisterUseCase
import com.snapwork.weatherappdemo.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    val userSession: StateFlow<String?> = getSessionUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _loginState = MutableStateFlow<UiState<Unit>?>(null)
    val loginState: StateFlow<UiState<Unit>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<Unit>?>(null)
    val registerState: StateFlow<UiState<Unit>?> = _registerState.asStateFlow()

    fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && emailRegex.matches(email)
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    fun login(email: String, password: String) {
        if (!validateEmail(email)) {
            _loginState.value = UiState.Error("Invalid email format")
            return
        }
        if (password.isBlank()) {
            _loginState.value = UiState.Error("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = loginUseCase(email, password)
            if (result.isSuccess) {
                _loginState.value = UiState.Success(Unit)
            } else {
                _loginState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, passwordConfirm: String) {
        if (!validateEmail(email)) {
            _registerState.value = UiState.Error("Invalid email format")
            return
        }
        if (!validatePassword(password)) {
            _registerState.value = UiState.Error("Password must be at least 6 characters")
            return
        }
        if (password != passwordConfirm) {
            _registerState.value = UiState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _registerState.value = UiState.Loading
            val result = registerUseCase(email, password)
            if (result.isSuccess) {
                _registerState.value = UiState.Success(Unit)
            } else {
                _registerState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            resetStates()
        }
    }

    fun resetStates() {
        _loginState.value = null
        _registerState.value = null
    }
}
