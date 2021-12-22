package ru.anasoft.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.anasoft.weather.model.RepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData(),
                    private val repositoryImpl: RepositoryImpl = RepositoryImpl()): ViewModel() {

    fun getLiveData():LiveData<AppState> {
        return liveData
    }

    fun getWeather() {
        getWeatherFromServer()
    }

    private fun getWeatherFromServer() {
        liveData.postValue(AppState.Loading(50))
        Thread {
            sleep(1000)
            val rand = (1..10).random()
            if (rand > 5) {
                liveData.postValue(AppState.Success(repositoryImpl.getWeatherFromServer()))
            } else {
                liveData.postValue(AppState.Error(IllegalStateException("Сервер недоступен")))
            }
        }.start()
    }

}