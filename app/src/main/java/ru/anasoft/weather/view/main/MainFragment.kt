package ru.anasoft.weather.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.anasoft.weather.R
import ru.anasoft.weather.databinding.FragmentMainBinding
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.view.details.DetailsFragment
import ru.anasoft.weather.viewmodel.AppState
import ru.anasoft.weather.viewmodel.MainViewModel

const val IS_RUSSIAN_KEY = "IS_RUSSIAN_KEY"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() {
            return _binding!!
        }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private var isRussian = true

    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            activity?.supportFragmentManager?.apply {
                beginTransaction()
                .add(R.id.container, DetailsFragment.newInstance(Bundle().apply {
                    putParcelable(DetailsFragment.BUNDLE_EXTRA, weather) }))
                .addToBackStack("")
                .commit()
            }
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        isRussian = requireActivity()
            .getPreferences(AppCompatActivity.MODE_PRIVATE)
            .getBoolean(IS_RUSSIAN_KEY, true)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            mainFragmentRecyclerView.adapter = adapter
            mainFragmentButtonChangeLocation.setOnClickListener {
                sendRequest()
            }
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppState> { renderData(it) })
        createListOfCities()
    }

    private fun createListOfCities() {
        with(binding) {
            if (isRussian) {
                viewModel.getListWeatherRus()
                mainFragmentButtonChangeLocation.setImageResource(R.drawable.ic_earth)
            }
            else {
                viewModel.getListWeatherWorld()
                mainFragmentButtonChangeLocation.setImageResource(R.drawable.ic_russia)
            }
        }
    }

    private fun sendRequest() {
        isRussian = !isRussian
        createListOfCities()

        requireActivity()
            .getPreferences(AppCompatActivity.MODE_PRIVATE).edit()
            .putBoolean(IS_RUSSIAN_KEY, isRussian)
            .apply()

    }

    private fun renderData(appState:AppState) {
        with(binding) {
            when (appState) {
                is AppState.Loading -> {
                    loadingLayout.visibility = View.VISIBLE
                }
                is AppState.SuccessListWeather -> {
                    loadingLayout.visibility = View.GONE
                    adapter.setWeather(appState.weatherData)
                }
                is AppState.Error -> {
                    loadingLayout.visibility = View.GONE
                    root.showSnackBar("Ошибка загрузки", "Повторить",
                        { with(viewModel) {
                            if (isRussian) {
                                getListWeatherRus()
                            } else {
                                getListWeatherWorld()
                            }
                        } }
                    )
                }
            }
        }
    }

    private fun View.showSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE) {

        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        adapter.removeListener()
    }

    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }

}
