package com.viplearner.weather.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.viplearner.weather.data.local.dao.UserPreferencesDao
import com.viplearner.weather.data.local.entity.UserPreferencesEntity

@Database(
    entities = [UserPreferencesEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        const val DATABASE_NAME = "weather_database"
    }
}
