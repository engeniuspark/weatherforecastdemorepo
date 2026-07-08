package com.snapwork.weatherapp.domain.usecase

import com.snapwork.weatherapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<String?> {
        return authRepository.getSession()
    }
}
