package com.snapwork.weatherapp.domain.repository

import com.snapwork.weatherapp.domain.model.WeatherHistoryItem
import com.snapwork.weatherapp.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun fetchWeather(latitude: Double, longitude: Double): Result<WeatherInfo>
    fun getWeatherHistory(): Flow<List<WeatherHistoryItem>>
    suspend fun saveWeatherHistory(weatherInfo: WeatherInfo): Result<Unit>
}
