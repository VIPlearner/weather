package com.viplearner.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.viplearner.weather.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreferences(): Flow<UserPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreferences(preferences: UserPreferencesEntity)

    @Update
    suspend fun updateUserPreferences(preferences: UserPreferencesEntity)

    @Query("UPDATE user_preferences SET favoriteCity = :cityName WHERE id = 1")
    suspend fun updateFavoriteCity(cityName: String)

    @Query("UPDATE user_preferences SET temperatureUnit = :unit WHERE id = 1")
    suspend fun updateTemperatureUnit(unit: String)
}
