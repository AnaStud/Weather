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

    fun getWeatherListFromLocalRus() = getDataFromLocal(isRussian = true)

    fun getWeatherListFromLocalWorld() = getDataFromLocal(isRussian = false)

    //fun getWeatherFromRemoteSource() = getDataFromLocalSource(isRussian = true)

    private fun getDataFromLocal(isRussian: Boolean) {
        liveData.value = AppState.Loading(50)
        Thread {
            sleep(1000)
            liveData.postValue(AppState.Success(
                if (isRussian) {
                    repositoryImpl.getWeatherListFromLocalRus()
                } else {
                    repositoryImpl.getWeatherListFromLocalWorld()
                }))
        }.start()
    }

}