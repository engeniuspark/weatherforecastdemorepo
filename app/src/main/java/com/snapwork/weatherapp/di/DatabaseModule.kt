package com.snapwork.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.snapwork.weatherapp.data.local.AppDatabase
import com.snapwork.weatherapp.data.local.dao.UserDao
import com.snapwork.weatherapp.data.local.dao.WeatherHistoryDao
import com.snapwork.weatherapp.data.local.datastore.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "weather_app_db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideWeatherHistoryDao(database: AppDatabase): WeatherHistoryDao {
        return database.weatherHistoryDao()
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context.applicationContext)
    }
}
