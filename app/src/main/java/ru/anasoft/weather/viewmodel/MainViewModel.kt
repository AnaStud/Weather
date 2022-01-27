package ru.anasoft.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.anasoft.weather.repository.RepositoryRemoteImpl
import java.lang.Thread.sleep

class MainViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData()): ViewModel() {

    private val repositoryImpl: RepositoryRemoteImpl by lazy {
        RepositoryRemoteImpl()
    }

    fun getLiveData() = liveData

    fun getListWeatherRus() = getListWeather(isRussian = true)

    fun getListWeatherWorld() = getListWeather(isRussian = false)

    private fun getListWeather(isRussian: Boolean) {
        liveData.value = AppState.Loading(50)
        Thread {
            sleep(1000)
            liveData.postValue(AppState.SuccessListWeather(
                with(repositoryImpl) {
                    if (isRussian) {
                        getListWeatherRus()
                    } else {
                        getListWeatherWorld()
                    }
                }
            ))
        }.start()
    }

}