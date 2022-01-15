package ru.anasoft.weather.view.details

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import ru.anasoft.weather.databinding.FragmentDetailsBinding
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO
import ru.anasoft.weather.utils.WeatherLoader

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    private lateinit var weatherBundle: Weather

    private val onLoadListener: WeatherLoader.WeatherLoaderListener =
        object : WeatherLoader.WeatherLoaderListener {

            override fun onLoaded(weatherDTO: WeatherDTO) {
                displayWeather(weatherDTO)
            }

            override fun onFailed(t: Throwable) {
                setWeatherData()
                Snackbar.make(binding.detailsFragment, t.toString(), Snackbar.LENGTH_INDEFINITE).show()
            }
        }


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
            WeatherLoader(onLoadListener).loadWeather(it.city.lt, it.city.ln)
        }
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
            condition.text = weatherDTO.fact?.condition
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