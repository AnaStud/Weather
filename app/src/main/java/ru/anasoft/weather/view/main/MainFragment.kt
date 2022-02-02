package ru.anasoft.weather.view.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main.*
import ru.anasoft.weather.R
import ru.anasoft.weather.databinding.FragmentMainBinding
import ru.anasoft.weather.model.City
import ru.anasoft.weather.model.Weather
import ru.anasoft.weather.view.contacts.REQUEST_CODE
import ru.anasoft.weather.view.details.DetailsFragment
import ru.anasoft.weather.viewmodel.AppState
import ru.anasoft.weather.viewmodel.MainViewModel
import java.io.IOException

const val IS_RUSSIAN_KEY = "IS_RUSSIAN_KEY"
const val REQUEST_CODE_LOCATION = 911

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
            mainFragmentButtonChangeLocation.setOnClickListener { sendRequest() }
            mainFragmentFABLocation.setOnClickListener { checkPermission() }
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppState> { renderData(it) })
        createListOfCities()
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showRequestPermissionRationale()
                }
                else -> {
                    myRequestPermission()
                }
            }
        }
    }

    private fun myRequestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
    }

    private fun showRequestPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.RationaleTitleLocation))
            .setMessage(getString(R.string.RationaleTextLocation))
            .setPositiveButton(getString(R.string.RationaleYes)) { _, _ -> myRequestPermission() }
            .setNegativeButton(getString(R.string.RationaleNo)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {

        if (requestCode == REQUEST_CODE_LOCATION) {
            val sizeGR = grantResults.size
            if (sizeGR != 0) {
                when {
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> {
                        getLocation()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                        showRequestPermissionRationale()
                    }
                }
            }
        }
    }

    private val MIN_DISTANCE = 100f
    private val REFRESH_PERIOD = 60000L
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) { getAddress(location) }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) { }
        override fun onProviderDisabled(provider: String) { }
        override fun onProviderEnabled(provider: String) { }
    }

    private fun getLocation(){
        activity?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED) {

                val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    val providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    providerGPS?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MIN_DISTANCE,
                            locationListener
                        )
                    }
                }
                else {
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lastLocation?.let{ itLocation ->
                        getAddress(itLocation)
                    }
                }
            }
            else { showRequestPermissionRationale() }
        }
    }

    private fun getAddress(location: Location){
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                mainFragmentFABLocation.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            }
            catch (e: IOException) { e.printStackTrace() }
        }.start()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    openDetailsFragment(Weather(City(address, location.latitude, location.longitude)))
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(weather: Weather) {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(
                    R.id.container,
                    DetailsFragment.newInstance(Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    })
                )
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
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
