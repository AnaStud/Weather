package ru.anasoft.weather.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.anasoft.weather.R
import ru.anasoft.weather.databinding.FragmentMainBinding
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.view.details.DetailsFragment
import ru.anasoft.weather.viewmodel.AppState
import ru.anasoft.weather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() {
            return _binding!!
        }

    private lateinit var viewModel: MainViewModel

    private var isRussian = true

    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            val manager = activity?.supportFragmentManager
            if (manager != null) {
                val bundle = Bundle()
                bundle.putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                manager.beginTransaction()
                    .add(R.id.container, DetailsFragment.newInstance(bundle))
                    .addToBackStack("")
                    .commit()
            }
        }
    })

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

        binding.mainFragmentRecyclerView.adapter = adapter
        binding.mainFragmentButtonChangeLocation.setOnClickListener {
            sendRequest()
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppState> { renderData(it) })
        viewModel.getWeatherListFromLocalRus()
    }

    private fun sendRequest() {
        if (isRussian) {
            viewModel.getWeatherListFromLocalWorld()
            binding.mainFragmentButtonChangeLocation.setImageResource(R.drawable.ic_russia)
        } else {
            viewModel.getWeatherListFromLocalRus()
            binding.mainFragmentButtonChangeLocation.setImageResource(R.drawable.ic_earth)
        }
        isRussian = !isRussian
    }

    private fun renderData(appState:AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Success -> {
                binding.loadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                    Snackbar.make(
                    binding.root,
                    "Ошибка загрузки",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Повторить") {
                        if (isRussian) {
                            viewModel.getWeatherListFromLocalRus()
                        } else {
                            viewModel.getWeatherListFromLocalWorld()
                        }
                    }.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        adapter.removeListener()
        super.onDestroy()
    }


    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }

}
