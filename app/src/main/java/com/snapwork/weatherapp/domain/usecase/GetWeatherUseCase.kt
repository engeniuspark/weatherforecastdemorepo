package com.snapwork.weatherapp.domain.usecase

import com.snapwork.weatherapp.domain.model.WeatherInfo
import com.snapwork.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<WeatherInfo> {
        return weatherRepository.fetchWeather(latitude, longitude)
    }
}
