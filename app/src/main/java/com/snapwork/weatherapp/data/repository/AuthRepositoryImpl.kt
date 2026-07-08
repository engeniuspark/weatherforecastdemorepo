package com.snapwork.weatherapp.data.repository

import com.snapwork.weatherapp.data.local.dao.UserDao
import com.snapwork.weatherapp.data.local.datastore.SessionManager
import com.snapwork.weatherapp.data.local.entity.UserEntity
import com.snapwork.weatherapp.domain.repository.AuthRepository
import com.snapwork.weatherappdemo.utils.HashUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("Email already exists"))
            }
            val hashedPassword = HashUtils.sha256(password)
            val user = UserEntity(email = email, hashedPassword = hashedPassword)
            userDao.insertUser(user)
            sessionManager.saveSession(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val user = userDao.getUserByEmail(email)
                ?: return Result.failure(Exception("Invalid email or password"))
            
            val hashedPassword = HashUtils.sha256(password)
            if (user.hashedPassword == hashedPassword) {
                sessionManager.saveSession(email)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSession(): Flow<String?> {
        return sessionManager.userSessionFlow
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            sessionManager.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
