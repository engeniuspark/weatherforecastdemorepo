package com.snapwork.weatherapp.presentation.weather

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapwork.weatherapp.domain.model.WeatherHistoryItem
import com.snapwork.weatherapp.domain.model.WeatherInfo
import com.snapwork.weatherapp.domain.usecase.GetWeatherHistoryUseCase
import com.snapwork.weatherapp.domain.usecase.GetWeatherUseCase
import com.snapwork.weatherapp.domain.usecase.SaveWeatherHistoryUseCase
import com.snapwork.weatherappdemo.presentation.UiState
import com.snapwork.weatherapp.utils.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject



@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val saveWeatherHistoryUseCase: SaveWeatherHistoryUseCase,
    private val getWeatherHistoryUseCase: GetWeatherHistoryUseCase,
    private val locationTracker: LocationTracker,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _weatherState = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val weatherState: StateFlow<UiState<WeatherInfo>> = _weatherState.asStateFlow()

    val historyState: StateFlow<List<WeatherHistoryItem>> = getWeatherHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var hasInitialFetchCompleted = false

    fun loadWeather(isRefresh: Boolean = false) {
        android.util.Log.d("WeatherViewModel", "loadWeather called (isRefresh = $isRefresh)")
        viewModelScope.launch {
            if (!isRefresh) {
                _weatherState.value = UiState.Loading
            }

            android.util.Log.d("WeatherViewModel", "Fetching location...")
            val location = kotlinx.coroutines.withTimeoutOrNull(8000) {
                locationTracker.getCurrentLocation()
            }
            android.util.Log.d("WeatherViewModel", "Location result: $location")
            if (location == null) {
                _weatherState.value = UiState.Error("Location unavailable or request timed out. Make sure GPS/Location services are enabled in your emulator/device settings and try again.")
                return@launch
            }

            android.util.Log.d("WeatherViewModel", "Fetching remote weather for lat=${location.latitude}, lon=${location.longitude}...")
            val result = kotlinx.coroutines.withTimeoutOrNull(8000) {
                getWeatherUseCase(location.latitude, location.longitude)
            }

            if (result != null && result.isSuccess) {
                val remoteInfo = result.getOrNull()
                android.util.Log.d("WeatherViewModel", "Remote weather success: $remoteInfo")
                if (remoteInfo != null) {
                    val resolvedInfo = resolveLocationNames(location.latitude, location.longitude, remoteInfo)
                    _weatherState.value = UiState.Success(resolvedInfo)

                    if (!hasInitialFetchCompleted) {
                        android.util.Log.d("WeatherViewModel", "Saving weather to local database history...")
                        saveWeatherHistoryUseCase(resolvedInfo)
                        hasInitialFetchCompleted = true
                    }
                } else {
                    _weatherState.value = UiState.Error("No weather data found")
                }
            } else {
                val errorMsg = if (result == null) {
                    "Weather request timed out. Please check your internet connection and try again."
                } else {
                    result.exceptionOrNull()?.message ?: "Failed to fetch weather data"
                }
                android.util.Log.e("WeatherViewModel", "Remote weather failure: $errorMsg")
                _weatherState.value = UiState.Error(errorMsg)
            }
        }
    }

    fun showError(message: String) {
        _weatherState.value = UiState.Error(message)
    }

    private suspend fun resolveLocationNames(lat: Double, lon: Double, info: WeatherInfo): WeatherInfo {
        return withContext(Dispatchers.IO) {
            try {
                // Wrap the blocking Geocoder call in a timeout block.
                // On some emulators or slow network links, geocoder.getFromLocation hangs indefinitely.
                kotlinx.coroutines.withTimeoutOrNull(3000) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    val address = addresses?.firstOrNull()
                    if (address != null) {
                        val resolvedCity = address.locality ?: address.subAdminArea ?: info.city
                        val resolvedCountry = address.countryName ?: info.country
                        info.copy(city = resolvedCity, country = resolvedCountry)
                    } else {
                        info
                    }
                } ?: info
            } catch (e: Exception) {
                info // Fallback on geocoder failure or cancellation
            }
        }
    }
}
