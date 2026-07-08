package com.snapwork.weatherapp.domain.usecase

import com.snapwork.weatherapp.domain.model.WeatherHistoryItem
import com.snapwork.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherHistoryUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(): Flow<List<WeatherHistoryItem>> {
        return weatherRepository.getWeatherHistory()
    }
}
