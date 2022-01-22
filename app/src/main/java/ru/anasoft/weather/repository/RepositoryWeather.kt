package ru.anasoft.weather.repository

import retrofit2.Callback
import ru.anasoft.weather.model.WeatherDTO

interface RepositoryWeather {
    fun getWeatherFromServer(lt:Double, ln:Double, callback:Callback<WeatherDTO>)
}