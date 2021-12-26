package ru.anasoft.weather.model

class RepositoryImpl:Repository {

    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherListFromLocalRus(): List<Weather> {
        return getRussianCities()
    }

    override fun getWeatherListFromLocalWorld(): List<Weather> {
        return getWorldCities()
    }

}