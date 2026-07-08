package com.snapwork.weatherapp.data.remote

import com.snapwork.weatherappdemo.data.remote.model.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): WeatherDto
}
