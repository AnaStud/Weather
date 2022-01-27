package ru.anasoft.weather.repository

import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO
import ru.anasoft.weather.model.WeatherHistory

interface RepositoryLocal {
    fun getAllHistory(): List<WeatherHistory>
    fun saveHistory(weather: Weather, weatherDTO: WeatherDTO)
}