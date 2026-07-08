package com.snapwork.weatherappdemo.domain.usecase

import com.snapwork.weatherappdemo.domain.model.WeatherInfo
import com.snapwork.weatherappdemo.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UseCasesTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var saveWeatherHistoryUseCase: SaveWeatherHistoryUseCase

    @BeforeEach
    fun setUp() {
        weatherRepository = mockk(relaxed = true)
        getWeatherUseCase = GetWeatherUseCase(weatherRepository)
        saveWeatherHistoryUseCase = SaveWeatherHistoryUseCase(weatherRepository)
    }

    @Test
    fun `GetWeatherUseCase invokes repository fetchWeather`() = runTest {
        val lat = 12.34
        val lon = 56.78
        val weatherInfo = WeatherInfo("Pune", "IN", 24.5, 1000L, 5000L, "Clear", 3000L)
        
        coEvery { weatherRepository.fetchWeather(lat, lon) } returns Result.success(weatherInfo)

        val result = getWeatherUseCase(lat, lon)

        assertTrue(result.isSuccess)
        assertEquals(weatherInfo, result.getOrNull())
        coVerify(exactly = 1) { weatherRepository.fetchWeather(lat, lon) }
    }

    @Test
    fun `SaveWeatherHistoryUseCase invokes repository saveWeatherHistory`() = runTest {
        val weatherInfo = WeatherInfo("Pune", "IN", 24.5, 1000L, 5000L, "Clear", 3000L)
        coEvery { weatherRepository.saveWeatherHistory(weatherInfo) } returns Result.success(Unit)

        val result = saveWeatherHistoryUseCase(weatherInfo)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { weatherRepository.saveWeatherHistory(weatherInfo) }
    }
}
