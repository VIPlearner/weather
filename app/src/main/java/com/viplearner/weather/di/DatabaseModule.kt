package com.viplearner.weather.di

import android.content.Context
import androidx.room.Room
import com.viplearner.weather.data.local.dao.UserPreferencesDao
import com.viplearner.weather.data.local.database.WeatherDatabase
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
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            WeatherDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideUserPreferencesDao(database: WeatherDatabase): UserPreferencesDao {
        return database.userPreferencesDao()
    }
}
