package com.snapwork.weatherappdemo.data.repository

import com.snapwork.weatherappdemo.data.local.dao.WeatherHistoryDao
import com.snapwork.weatherappdemo.data.remote.WeatherApiService
import com.snapwork.weatherappdemo.data.remote.model.MainDto
import com.snapwork.weatherappdemo.data.remote.model.SysDto
import com.snapwork.weatherappdemo.data.remote.model.WeatherConditionDto
import com.snapwork.weatherappdemo.data.remote.model.WeatherDto
import com.snapwork.weatherappdemo.domain.model.WeatherInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WeatherRepositoryTest {

    private lateinit var apiService: WeatherApiService
    private lateinit var historyDao: WeatherHistoryDao
    private lateinit var repository: WeatherRepositoryImpl
    private val apiKey = "test_api_key"

    @BeforeEach
    fun setUp() {
        apiService = mockk()
        historyDao = mockk(relaxed = true)
        repository = WeatherRepositoryImpl(apiService, historyDao, apiKey)
    }

    @Test
    fun `fetchWeather success converts Kelvin to Celsius and returns WeatherInfo`() = runTest {
        val lat = 12.34
        val lon = 56.78
        
        val apiResponse = WeatherDto(
            name = "Mumbai",
            sys = SysDto(country = "IN", sunrise = 1600000000L, sunset = 1600040000L),
            main = MainDto(temp = 300.15), // 300.15K - 273.15 = 27.0°C
            weather = listOf(WeatherConditionDto(main = "Rain", description = "heavy rain", icon = "10d")),
            dt = 1600050000L
        )

        coEvery { apiService.getCurrentWeather(lat, lon, apiKey) } returns apiResponse

        val result = repository.fetchWeather(lat, lon)

        assertTrue(result.isSuccess)
        val info = result.getOrNull()!!
        assertEquals("Mumbai", info.city)
        assertEquals("IN", info.country)
        assertEquals(27.0, info.temperatureCelsius, 0.01)
        assertEquals(1600000000L, info.sunrise)
        assertEquals(1600040000L, info.sunset)
        assertEquals("Rain", info.condition)
        assertEquals(1600050000L, info.timestamp)
    }

    @Test
    fun `fetchWeather failure returns failure Result`() = runTest {
        val lat = 12.34
        val lon = 56.78
        val exception = RuntimeException("API failure or Invalid API Key")

        coEvery { apiService.getCurrentWeather(lat, lon, apiKey) } throws exception

        val result = repository.fetchWeather(lat, lon)

        assertTrue(result.isFailure)
        assertEquals("API failure or Invalid API Key", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveWeatherHistory success calls historyDao`() = runTest {
        val weatherInfo = WeatherInfo(
            city = "Pune",
            country = "IN",
            temperatureCelsius = 25.0,
            sunrise = 1600000000L,
            sunset = 1600040000L,
            condition = "Clouds",
            timestamp = 1600050000L
        )

        coEvery { historyDao.insertWeatherHistory(any()) } returns 1L

        val result = repository.saveWeatherHistory(weatherInfo)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { 
            historyDao.insertWeatherHistory(match { 
                it.city == "Pune" && 
                it.country == "IN" && 
                it.temperature == 25.0 && 
                it.sunrise == 1600000000L && 
                it.sunset == 1600040000L && 
                it.condition == "Clouds" && 
                it.timestamp == 1600050000L 
            }) 
        }
    }
}
