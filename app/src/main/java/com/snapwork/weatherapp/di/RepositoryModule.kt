package com.snapwork.weatherapp.di


import com.snapwork.weatherapp.data.repository.AuthRepositoryImpl
import com.snapwork.weatherapp.data.repository.WeatherRepositoryImpl
import com.snapwork.weatherapp.domain.repository.AuthRepository
import com.snapwork.weatherapp.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}
