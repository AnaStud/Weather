package ru.anasoft.weather.viewmodel

import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO

sealed class AppState {
    data class Loading(val progress:Int):AppState()
    data class SuccessListWeather(val weatherData: List<Weather>):AppState()
    data class SuccessWeatherDTO(val weatherDTO: WeatherDTO):AppState()
    data class Error(val error:Throwable):AppState()
}
