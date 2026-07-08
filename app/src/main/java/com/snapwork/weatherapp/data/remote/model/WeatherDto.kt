package com.snapwork.weatherappdemo.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    @SerialName("name") val name: String? = null,
    @SerialName("sys") val sys: SysDto? = null,
    @SerialName("main") val main: MainDto? = null,
    @SerialName("weather") val weather: List<WeatherConditionDto>? = null,
    @SerialName("dt") val dt: Long? = null
)

@Serializable
data class SysDto(
    @SerialName("country") val country: String? = null,
    @SerialName("sunrise") val sunrise: Long? = null,
    @SerialName("sunset") val sunset: Long? = null
)

@Serializable
data class MainDto(
    @SerialName("temp") val temp: Double? = null
)

@Serializable
data class WeatherConditionDto(
    @SerialName("main") val main: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("icon") val icon: String? = null
)
