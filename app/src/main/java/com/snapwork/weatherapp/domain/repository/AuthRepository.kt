package com.snapwork.weatherapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    fun getSession(): Flow<String?>
    suspend fun logout(): Result<Unit>
}
