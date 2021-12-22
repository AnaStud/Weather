package ru.anasoft.weather.model

class RepositoryImpl:Repository {

    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherFromLocal(): Weather {
        return Weather()
    }

}