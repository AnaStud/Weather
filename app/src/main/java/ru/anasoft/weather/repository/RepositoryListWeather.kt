package ru.anasoft.weather.repository

import ru.anasoft.weather.model.Weather

interface RepositoryListWeather {
    fun getListWeatherRus(): List<Weather>
    fun getListWeatherWorld(): List<Weather>
}