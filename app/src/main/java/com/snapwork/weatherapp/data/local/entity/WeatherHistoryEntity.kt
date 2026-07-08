package com.snapwork.weatherappdemo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_history")
data class WeatherHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val city: String,
    val country: String,
    val temperature: Double,
    val sunrise: Long,
    val sunset: Long,
    val condition: String,
    val timestamp: Long
)
