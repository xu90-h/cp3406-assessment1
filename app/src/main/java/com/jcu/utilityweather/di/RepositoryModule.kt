package com.jcu.utilityweather.di

import com.jcu.utilityweather.data.repository.FakeWeatherRepository
import com.jcu.utilityweather.data.repository.WeatherRepository
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
    abstract fun bindWeatherRepository(impl: FakeWeatherRepository): WeatherRepository
}
