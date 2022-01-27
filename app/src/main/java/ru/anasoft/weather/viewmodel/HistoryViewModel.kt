package ru.anasoft.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.anasoft.weather.repository.RepositoryLocal
import ru.anasoft.weather.repository.RepositoryLocalImpl

class HistoryViewModel(
    private val liveData: MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryLocal: RepositoryLocal = RepositoryLocalImpl()) : ViewModel() {

    @JvmName("getLiveData1")
    fun getLiveData() = liveData

    fun getAllHistory() {
        Thread {
            liveData.postValue(AppState.SuccessHistory(repositoryLocal.getAllHistory()))
        }.start()
    }
}