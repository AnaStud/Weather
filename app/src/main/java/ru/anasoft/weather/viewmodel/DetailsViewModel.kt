package ru.anasoft.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO
import ru.anasoft.weather.repository.RepositoryRemoteImpl
import ru.anasoft.weather.repository.RepositoryLocalImpl

class DetailsViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData()): ViewModel() {

    private val repositoryRemoteImpl: RepositoryRemoteImpl by lazy {
        RepositoryRemoteImpl()
    }

    private val repositoryLocalImpl: RepositoryLocalImpl by lazy {
        RepositoryLocalImpl()
    }

    fun getLiveData() = liveData

    fun getWeatherDTOFromServer(lt:Double, ln:Double) {
        repositoryRemoteImpl.getWeatherFromServer(lt, ln, callback)
    }

    private val callback = object: Callback<WeatherDTO> {
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            liveData.value = AppState.Loading(50)
            if (response.isSuccessful) {
                response.body()?.let {
                    liveData.postValue(AppState.SuccessWeatherDTO(it))
                }
            }
            else {
                liveData.value = AppState.Error(Throwable(response.message()))
            }
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            AppState.Error(t)
        }
    }

    fun saveCityToDB(weather: Weather, weatherDTO: WeatherDTO) {
        repositoryLocalImpl.saveHistory(weather, weatherDTO)
    }
}