package ru.anasoft.weather.repository

import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.anasoft.weather.BuildConfig
import ru.anasoft.weather.model.WeatherDTO
import ru.anasoft.weather.model.getRussianCities
import ru.anasoft.weather.model.getWorldCities
import ru.anasoft.weather.utils.REQUEST_SERVER

class RepositoriesImpl: RepositoryListWeather, RepositoryWeather {

    override fun getListWeatherRus() = getRussianCities()
    override fun getListWeatherWorld() = getWorldCities()

    override fun getWeatherFromServer(lt:Double, ln:Double, callback:Callback<WeatherDTO>) {
        val retrofit = Retrofit.Builder()
            .baseUrl(REQUEST_SERVER)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build().create(WeatherApi::class.java)
        retrofit.getWeather(BuildConfig.WEATHER_API_KEY, lt, ln).enqueue(callback)
    }

}