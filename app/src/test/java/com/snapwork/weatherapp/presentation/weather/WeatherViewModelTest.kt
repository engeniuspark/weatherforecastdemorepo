package com.snapwork.weatherappdemo.presentation.weather

import android.content.Context
import android.location.Location
import com.snapwork.weatherappdemo.domain.model.WeatherInfo
import com.snapwork.weatherappdemo.domain.usecase.GetWeatherHistoryUseCase
import com.snapwork.weatherappdemo.domain.usecase.GetWeatherUseCase
import com.snapwork.weatherappdemo.domain.usecase.SaveWeatherHistoryUseCase
import com.snapwork.weatherappdemo.presentation.UiState
import com.snapwork.weatherapp.utils.LocationTracker
import io.mockk.coEvery
import io.mockk.coVerify
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
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var saveWeatherHistoryUseCase: SaveWeatherHistoryUseCase
    private lateinit var getWeatherHistoryUseCase: GetWeatherHistoryUseCase
    private lateinit var locationTracker: LocationTracker
    private lateinit var context: Context
    private lateinit var viewModel: WeatherViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        io.mockk.mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0

        getWeatherUseCase = mockk()
        saveWeatherHistoryUseCase = mockk(relaxed = true)
        getWeatherHistoryUseCase = mockk()
        locationTracker = mockk()
        context = mockk(relaxed = true)

        every { getWeatherHistoryUseCase() } returns flowOf(emptyList())

        viewModel = WeatherViewModel(
            getWeatherUseCase,
            saveWeatherHistoryUseCase,
            getWeatherHistoryUseCase,
            locationTracker,
            context
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        io.mockk.unmockkAll()
    }

    @Test
    fun `loadWeather success retrieves location and fetches weather then saves history on first successful fetch`() = runTest {
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns 12.34
        every { mockLocation.longitude } returns 56.78
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation

        val weatherInfo = WeatherInfo(
            city = "Mumbai",
            country = "IN",
            temperatureCelsius = 28.0,
            sunrise = 1000L,
            sunset = 5000L,
            condition = "Clouds",
            timestamp = 3000L
        )

        coEvery { getWeatherUseCase(12.34, 56.78) } returns Result.success(weatherInfo)
        coEvery { saveWeatherHistoryUseCase(any()) } returns Result.success(Unit)

        viewModel.loadWeather()

        // Wait for real Dispatchers.IO background thread (Geocoder resolution) to complete
        var elapsed = 0L
        while (viewModel.weatherState.value is UiState.Loading && elapsed < 2000) {
            kotlinx.coroutines.delay(50)
            elapsed += 50
        }

        val state = viewModel.weatherState.value
        assertTrue(state is UiState.Success)
        assertEquals("Mumbai", (state as UiState.Success).data.city)

        coVerify(exactly = 1) { saveWeatherHistoryUseCase(any()) }
    }

    @Test
    fun `loadWeather success subsequent calls do not save history again`() = runTest {
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns 12.34
        every { mockLocation.longitude } returns 56.78
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation

        val weatherInfo = WeatherInfo("Mumbai", "IN", 28.0, 1000L, 5000L, "Clouds", 3000L)
        coEvery { getWeatherUseCase(12.34, 56.78) } returns Result.success(weatherInfo)
        coEvery { saveWeatherHistoryUseCase(any()) } returns Result.success(Unit)

        viewModel.loadWeather()

        // Wait for real Dispatchers.IO background thread to complete
        var elapsed = 0L
        while (viewModel.weatherState.value is UiState.Loading && elapsed < 2000) {
            kotlinx.coroutines.delay(50)
            elapsed += 50
        }

        viewModel.loadWeather(isRefresh = true)

        elapsed = 0L
        while (viewModel.weatherState.value is UiState.Loading && elapsed < 2000) {
            kotlinx.coroutines.delay(50)
            elapsed += 50
        }

        coVerify(exactly = 1) { saveWeatherHistoryUseCase(any()) }
    }

    @Test
    fun `loadWeather location unavailable sets error state`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns null

        viewModel.loadWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.weatherState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).message.contains("Location unavailable"))
    }

    @Test
    fun `loadWeather api failure sets error state`() = runTest {
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns 12.34
        every { mockLocation.longitude } returns 56.78
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation
        coEvery { getWeatherUseCase(12.34, 56.78) } returns Result.failure(Exception("API Error"))

        viewModel.loadWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.weatherState.value
        assertTrue(state is UiState.Error)
        assertEquals("API Error", (state as UiState.Error).message)
    }
}

