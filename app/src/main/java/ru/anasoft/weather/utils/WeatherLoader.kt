package ru.anasoft.weather.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import ru.anasoft.weather.BuildConfig
import ru.anasoft.weather.model.WeatherDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class WeatherLoader(private val listener: WeatherLoaderListener) {

    interface WeatherLoaderListener {
        fun onLoaded(weatherDTO: WeatherDTO)
        fun onFailed(throwable: Throwable)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun loadWeather(lt: Double, ln: Double) {

        Thread {

            try {
                //throw Throwable("test") // для проверки ошибки
                val uri = URL(
                    "https://api.weather.yandex.ru/v2/informers?lat=$lt&lon=$ln"
                )
                val urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                    requestMethod = "GET"
                    readTimeout = 2000
                    addRequestProperty("X-Yandex-API-Key", BuildConfig.WEATHER_API_KEY) // "123") // для проверки некорректного запроса
                }
                val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val weatherString = getLines(bufferedReader)
                val weatherDTO: WeatherDTO? = Gson().fromJson(weatherString, WeatherDTO::class.java)

                Handler(Looper.getMainLooper()).post {
                    if (weatherDTO != null) {
                        listener.onLoaded(weatherDTO)
                    }
                    else {
                        listener.onFailed(Throwable("weatherDTO is null"))
                    }
                }
                urlConnection.disconnect()
            }
            catch (t: Throwable) {
                Handler(Looper.getMainLooper()).post { listener.onFailed(t) }
            }
        }.start()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

}