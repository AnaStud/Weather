package ru.anasoft.weather.model

interface Repository {
    fun getWeatherFromServer(): Weather
    fun getWeatherFromLocal(): Weather
}