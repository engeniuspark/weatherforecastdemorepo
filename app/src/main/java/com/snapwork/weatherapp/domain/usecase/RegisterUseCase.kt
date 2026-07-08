package com.snapwork.weatherapp.domain.usecase

import com.snapwork.weatherapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.register(email, password)
    }
}
