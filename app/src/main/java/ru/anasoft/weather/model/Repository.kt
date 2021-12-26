package ru.anasoft.weather.model

interface Repository {
    fun getWeatherFromServer(): Weather
    fun getWeatherListFromLocalRus(): List<Weather>
    fun getWeatherListFromLocalWorld(): List<Weather>
}