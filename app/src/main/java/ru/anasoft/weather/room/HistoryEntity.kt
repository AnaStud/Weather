package ru.anasoft.weather.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val city: String,
    val lt: Double,
    val ln: Double,
    val dateTime: String,
    val temp: Int,
    val feelsLike: Int,
    val condition: String,
    val icon: String
)