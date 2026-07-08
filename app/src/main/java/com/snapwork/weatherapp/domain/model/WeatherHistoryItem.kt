package com.snapwork.weatherapp.domain.model

data class WeatherHistoryItem(
    val id: Long,
    val city: String,
    val country: String,
    val temperatureCelsius: Double,
    val sunrise: Long,
    val sunset: Long,
    val condition: String,
    val timestamp: Long
)
