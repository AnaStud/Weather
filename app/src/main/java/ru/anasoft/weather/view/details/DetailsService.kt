package ru.anasoft.weather.view.details

import android.app.IntentService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import ru.anasoft.weather.BuildConfig
import ru.anasoft.weather.model.WeatherDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

const val LT_KEY = "LT_KEY"
const val LN_KEY = "LN_KEY"
const val REQUEST_SERVER = "https://api.weather.yandex.ru";
const val REQUEST_PATH = "/v2/informers"
const val REQUEST_GET = "GET"
const val REQUEST_TIMEOUT = 10000
const val REQUEST_API_KEY = "X-Yandex-API-Key"

class DetailsService(name:String = "DetailsService") : IntentService(name) {

    private val broadcastIntent = Intent(DETAILS_INTENT_FILTER)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val lt = it.getDoubleExtra(LT_KEY, 0.0)
            val ln = it.getDoubleExtra(LN_KEY, 0.0)
            loadWeather(lt, ln)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadWeather(lt: Double, ln: Double) {
        try {
            //throw Throwable("test") // для проверки ошибки
            val url = URL("$REQUEST_SERVER$REQUEST_PATH?lat=$lt&lon=$ln")
            val urlConnection = (url.openConnection() as HttpsURLConnection).apply {
                requestMethod = REQUEST_GET
                readTimeout = REQUEST_TIMEOUT
                addRequestProperty(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
            }

            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val weatherString = getLines(bufferedReader)

            val weatherDTO: WeatherDTO? = Gson().fromJson(weatherString, WeatherDTO::class.java)
            broadcastIntent.putExtra(WEATHER_DTO_KEY, weatherDTO)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

            urlConnection.disconnect()
        }
        catch (t: Throwable) {
            broadcastIntent.putExtra(DETAILS_REQUEST_ERROR, t.message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

}