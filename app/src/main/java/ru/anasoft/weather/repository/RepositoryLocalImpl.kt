package ru.anasoft.weather.repository

import android.annotation.SuppressLint
import ru.anasoft.weather.app.App
import ru.anasoft.weather.model.City
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO
import ru.anasoft.weather.model.WeatherHistory
import ru.anasoft.weather.room.HistoryEntity
import java.text.SimpleDateFormat
import java.util.*

class RepositoryLocalImpl : RepositoryLocal {

    override fun getAllHistory(): List<WeatherHistory> {
        return convertHistoryEntityToWeather(App.getHistoryDao().getAllHistory())
    }

    override fun saveHistory(weather: Weather, weatherDTO: WeatherDTO) {
        Thread {
            App.getHistoryDao().insert(convertWeatherToEntity(weather, weatherDTO))
        }.start()
    }

    private fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>): List<WeatherHistory> {
        return entityList.map {
            WeatherHistory(City(it.city, it.lt, it.ln),
                it.dateTime, it.temp, it.feelsLike, it.condition, it.icon)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertWeatherToEntity(weather: Weather, weatherDTO: WeatherDTO): HistoryEntity {

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val currentDateTime = sdf.format(Date())

        return HistoryEntity(
            0,
            weather.city.name,
            weather.city.lt,
            weather.city.ln,
            currentDateTime,
            weatherDTO.fact?.temp ?: 0,
            weatherDTO.fact?.feelsLike ?: 0,
            weatherDTO.fact?.condition ?: "",
            weatherDTO.fact?.icon ?: ""
        )
    }

}