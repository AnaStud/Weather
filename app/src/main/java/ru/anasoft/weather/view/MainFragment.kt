package ru.anasoft.weather.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.anasoft.weather.databinding.FragmentMainBinding
import ru.anasoft.weather.viewmodel.AppState
import ru.anasoft.weather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() {
            return _binding!!
        }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppState> { renderData(it) })
        viewModel.getWeather()
    }

    private fun renderData(appState:AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Success -> {
                binding.loadingLayout.visibility = View.GONE
                binding.city.text = appState.weatherData.city.name
                binding.coordinates.text =
                    "Широта: ${appState.weatherData.city.lt}, Долгота: ${appState.weatherData.city.ln}"
                binding.temperature.text =  "${appState.weatherData.temperature}"
                binding.feels.text =  "${appState.weatherData.feels}"
                binding.mainFragment

                Snackbar.make(
                    binding.mainFragment,
                    "Загрузка завершена",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar.make(
                    binding.mainFragment,
                    "Ошибка загрузки",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Повторить") {
                        viewModel.getWeather()
                    }.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}