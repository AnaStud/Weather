package ru.anasoft.weather.view.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.anasoft.weather.databinding.FragmentDetailsBinding
import ru.anasoft.weather.model.Weather

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
            setWeatherData(it)
        }
    }

    private fun setWeatherData(weather: Weather) {
        with(binding) {
            weather.city.also { city ->
                cityName.text = city.name
                coordinateLt.text = city.lt.toString()
                coordinateLn.text = city.ln.toString()
                condition.text = "Облачно"
            }
            temperature.text = weather.temperature.toString()
            feels.text = weather.feels.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}