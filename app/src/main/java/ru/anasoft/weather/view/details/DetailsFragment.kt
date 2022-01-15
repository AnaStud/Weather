package ru.anasoft.weather.view.details

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import ru.anasoft.weather.databinding.FragmentDetailsBinding
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    private lateinit var weatherBundle: Weather

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        const val BUNDLE_EXTRA = "CITY_KEY"

        @JvmStatic
        fun newInstance(bundle:Bundle) = DetailsFragment().apply {
            arguments = bundle
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
            weatherBundle = it
            loadWeather(it.city.lt, it.city.ln)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadWeather(lt: Double, ln: Double) {

        try {
            Thread {
                val uri = URL(
                    "https://api.weather.yandex.ru/v2/informers?lat=$lt&lon=$ln"
                )
                val urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                    requestMethod = "GET"
                    readTimeout = 2000
                    addRequestProperty("X-Yandex-API-Key", "0e879387-4ac3-47db-bdd7-49238af4880b")
                }
                val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val weatherString = getLines(bufferedReader)
                val weatherDTO: WeatherDTO? = Gson().fromJson(weatherString, WeatherDTO::class.java)

                Handler(Looper.getMainLooper()).post {
                    if (weatherDTO != null) {
                        displayWeather(weatherDTO)
                    }
                    else {
                        setWeatherData()
                    }
                }
                urlConnection.disconnect()
            }.start()
        }
        catch (e: Exception) {
            //Log.e("", "Fail connection", e)
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            weatherBundle.city.also { city ->
                cityName.text = city.name
                coordinateLt.text = city.lt.toString()
                coordinateLn.text = city.ln.toString()

            }
            temperature.text = weatherDTO.fact?.temp.toString()
            feels.text = weatherDTO.fact?.feelsLike.toString()
            condition.text = weatherDTO.fact?.condition.toString()
        }
    }

    private fun setWeatherData() {
        with(binding) {
            weatherBundle.city.also { city ->
                cityName.text = city.name
                coordinateLt.text = city.lt.toString()
                coordinateLn.text = city.ln.toString()
                condition.text = "Облачно"
            }
            temperature.text = weatherBundle.temperature.toString()
            feels.text = weatherBundle.feels.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}