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
        fun newInstance(bundle:Bundle):DetailsFragment {
             val fragment  = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weather = arguments?.getParcelable<Weather>(BUNDLE_EXTRA)
        if(weather != null){
            setWeatherData(weather)
        }
    }

    private fun setWeatherData(weather: Weather) {
        binding.city.text = weather.city.name
        binding.coordinateLt.text = weather.city.lt.toString()
        binding.coordinateLn.text = weather.city.ln.toString()
        binding.temperature.text =  weather.temperature.toString()
        binding.feels.text = weather.feels.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}