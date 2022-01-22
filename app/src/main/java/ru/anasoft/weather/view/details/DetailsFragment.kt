package ru.anasoft.weather.view.details

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.anasoft.weather.databinding.FragmentDetailsBinding
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.model.WeatherDTO
import ru.anasoft.weather.viewmodel.AppState
import ru.anasoft.weather.viewmodel.DetailsViewModel

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    private lateinit var weatherBundle: Weather

    companion object {

        const val BUNDLE_EXTRA = "CITY_KEY"

        @JvmStatic
        fun newInstance(bundle:Bundle) = DetailsFragment().apply {
            arguments = bundle
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
        with(viewModel) {
            getLiveData().observe(viewLifecycleOwner, Observer<AppState> { renderData(it) })
        }
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
            weatherBundle = it
            viewModel.getWeatherDTOFromServer(it.city.lt, it.city.ln)
        }
    }

    private fun renderData(appState:AppState) {
        with(binding) {
            when (appState) {
                is AppState.Loading -> {
                    loadingLayout.visibility = View.VISIBLE
                }
                is AppState.SuccessWeatherDTO -> {
                    loadingLayout.visibility = View.GONE
                    displayWeather(appState.weatherDTO)
                }
                is AppState.Error -> {
                    loadingLayout.visibility = View.GONE
                    setWeatherData()
                    Snackbar
                        .make(root,
                            "Ошибка загрузки: ${appState.error.message}",
                            Snackbar.LENGTH_INDEFINITE)
                        .show()
                }
            }
        }
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
            temperature.text = weatherBundle.temp.toString()
            feels.text = weatherBundle.feels.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}