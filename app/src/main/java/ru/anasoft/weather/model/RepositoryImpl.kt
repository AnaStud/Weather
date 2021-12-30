package ru.anasoft.weather.model

class RepositoryImpl:Repository {

    override fun getWeatherFromServer() = Weather()

    override fun getWeatherListFromLocalRus() = getRussianCities()

    override fun getWeatherListFromLocalWorld() = getWorldCities()

}