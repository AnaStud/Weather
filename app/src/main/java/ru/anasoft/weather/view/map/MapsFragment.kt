package ru.anasoft.weather.view.map

import android.graphics.Color
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import ru.anasoft.weather.R
import ru.anasoft.weather.databinding.FragmentMapsMainBinding

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsMainBinding? = null
    private val binding: FragmentMapsMainBinding
        get() {
            return _binding!!
        }

    private lateinit var map: GoogleMap
    private val markers: ArrayList<Marker> = arrayListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val defaultCity = LatLng(55.7522, 37.6156)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultCity))
        googleMap.setOnMapLongClickListener {
            getAddress(it)
            addMarker(it)
            drawLine()
        }
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun getAddress(location: LatLng) {
        Thread {
            val geocoder = Geocoder(requireContext())
            val listAddress = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            requireActivity().runOnUiThread {
                binding.textAddress.text = listAddress[0].getAddressLine(0)
            }
        }.start()
    }

    private fun addMarker(location: LatLng) {
        val marker = map.addMarker(
            MarkerOptions().position(location)
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
        )
        marker?.let { markers.add(it) }
    }

    private fun drawLine() {
        val lastIndex = (markers.size - 1)
        if (lastIndex > 0) {
            map.addPolyline(
                PolylineOptions().add(markers[lastIndex].position, markers[lastIndex - 1].position)
                    .color(Color.MAGENTA)
                    .width(10f)
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapsMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        binding.buttonSearch.setOnClickListener { searchLocation() }
    }

    private fun searchLocation(){
        Thread {
            val geocoder = Geocoder(requireContext())
            val listAddress = geocoder.getFromLocationName(binding.searchAddress.text.toString(),1)
            val myLocation = LatLng(listAddress[0].latitude,listAddress[0].longitude)
            requireActivity().runOnUiThread {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,10f))
                map.addMarker(MarkerOptions().position(myLocation).title(""))
            }
        }.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}