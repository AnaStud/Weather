package ru.anasoft.weather.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class WeatherDTO(
    val fact: FactDTO?
    ): Parcelable

@Parcelize
data class FactDTO(
    val temp: Int?,

    @SerializedName("feels_like")
    val feelsLike: Int?,

    val condition: String?,
    val icon: String
): Parcelable

