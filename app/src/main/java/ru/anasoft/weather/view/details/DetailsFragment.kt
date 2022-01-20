package ru.anasoft.weather.view.details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import ru.anasoft.weather.databinding.FragmentDetailsBinding
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO

const val WEATHER_DTO_KEY = "WEATHER DTO KEY"
const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_REQUEST_ERROR = "REQUEST ERROR"

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    private lateinit var weatherBundle: Weather

    companion object {

        const val BUNDLE_EXTRA = "CITY_KEY"

        @JvmStatic
        fun newInstance(bundle:Bundle) = DetailsFragment().apply {
            arguments = bundle
        }

    }

    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val weatherDTO = it.getParcelableExtra<WeatherDTO>(WEATHER_DTO_KEY)
                if (weatherDTO != null) {
                    displayWeather(weatherDTO)
                } else {
                    setWeatherData()
                }
                val throwableMessage = it.getStringExtra(DETAILS_REQUEST_ERROR)
                if (throwableMessage != null) {
                    Snackbar
                        .make(binding.detailsFragment,
                            "Ошибка загрузки с сервера: $throwableMessage",
                            Snackbar.LENGTH_INDEFINITE)
                        .show()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
            weatherBundle = it
            getWeather()
        }
    }

    private fun getWeather() {
        binding.detailsFragment.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        requireActivity().startService(
            Intent(requireActivity(), DetailsService::class.java).apply {
                putExtra(LT_KEY, weatherBundle.city.lt)
                putExtra(LN_KEY, weatherBundle.city.ln)
            }
        )
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            detailsFragment.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE
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
            detailsFragment.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }

}