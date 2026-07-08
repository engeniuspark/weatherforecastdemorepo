package com.snapwork.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snapwork.weatherappdemo.data.local.entity.WeatherHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherHistory(history: WeatherHistoryEntity): Long

    @Query("SELECT * FROM weather_history ORDER BY timestamp DESC")
    fun getAllWeatherHistory(): Flow<List<WeatherHistoryEntity>>
}
