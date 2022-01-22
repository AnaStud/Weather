package ru.anasoft.weather.repository

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.anasoft.weather.model.WeatherDTO
import ru.anasoft.weather.utils.REQUEST_API_KEY
import ru.anasoft.weather.utils.REQUEST_PATH

interface WeatherApi {
    @GET(REQUEST_PATH)
    fun getWeather(
        @Header(REQUEST_API_KEY) apikey: String,
        @Query("lat") lt: Double,
        @Query("lon") ln: Double
    ): Call<WeatherDTO>
}