package com.snapwork.weatherapp.domain.usecase

import com.snapwork.weatherapp.domain.model.WeatherInfo
import com.snapwork.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class SaveWeatherHistoryUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(weatherInfo: WeatherInfo): Result<Unit> {
        return weatherRepository.saveWeatherHistory(weatherInfo)
    }
}
