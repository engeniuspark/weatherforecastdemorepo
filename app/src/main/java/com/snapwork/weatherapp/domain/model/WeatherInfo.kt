package com.snapwork.weatherapp.domain.model

data class WeatherInfo(
    val city: String,
    val country: String,
    val temperatureCelsius: Double,
    val sunrise: Long,
    val sunset: Long,
    val condition: String,
    val timestamp: Long
)
