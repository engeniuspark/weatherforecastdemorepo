package com.snapwork.weatherapp.data.repository

import com.snapwork.weatherapp.data.local.dao.WeatherHistoryDao
import com.snapwork.weatherappdemo.data.local.entity.WeatherHistoryEntity
import com.snapwork.weatherapp.data.remote.WeatherApiService
import com.snapwork.weatherapp.domain.model.WeatherHistoryItem
import com.snapwork.weatherapp.domain.model.WeatherInfo
import com.snapwork.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val historyDao: WeatherHistoryDao,
    @Named("openWeatherApiKey") private val apiKey: String
) : WeatherRepository {

    override suspend fun fetchWeather(latitude: Double, longitude: Double): Result<WeatherInfo> {
        return try {
            val dto = apiService.getCurrentWeather(latitude, longitude, apiKey)
            val city = dto.name ?: "Unknown"
            val country = dto.sys?.country ?: "Unknown"
            val tempKelvin = dto.main?.temp ?: 273.15
            val tempCelsius = tempKelvin - 273.15
            val sunrise = dto.sys?.sunrise ?: 0L
            val sunset = dto.sys?.sunset ?: 0L
            val condition = dto.weather?.firstOrNull()?.main ?: "Clear"
            val timestamp = dto.dt ?: (System.currentTimeMillis() / 1000)

            val weatherInfo = WeatherInfo(
                city = city,
                country = country,
                temperatureCelsius = tempCelsius,
                sunrise = sunrise,
                sunset = sunset,
                condition = condition,
                timestamp = timestamp
            )
            Result.success(weatherInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getWeatherHistory(): Flow<List<WeatherHistoryItem>> {
        return historyDao.getAllWeatherHistory().map { list ->
            list.map { entity ->
                WeatherHistoryItem(
                    id = entity.id,
                    city = entity.city,
                    country = entity.country,
                    temperatureCelsius = entity.temperature,
                    sunrise = entity.sunrise,
                    sunset = entity.sunset,
                    condition = entity.condition,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun saveWeatherHistory(weatherInfo: WeatherInfo): Result<Unit> {
        return try {
            val entity = WeatherHistoryEntity(
                city = weatherInfo.city,
                country = weatherInfo.country,
                temperature = weatherInfo.temperatureCelsius,
                sunrise = weatherInfo.sunrise,
                sunset = weatherInfo.sunset,
                condition = weatherInfo.condition,
                timestamp = weatherInfo.timestamp
            )
            historyDao.insertWeatherHistory(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
