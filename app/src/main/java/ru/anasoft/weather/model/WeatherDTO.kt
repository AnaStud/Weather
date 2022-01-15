package ru.anasoft.weather.model

import com.google.gson.annotations.SerializedName

class WeatherDTO(
    val fact: FactDTO?
    )

data class FactDTO(
    val temp: Int?,

    @SerializedName("feels_like")
    val feelsLike: Int?,

    val condition: String?
    )

