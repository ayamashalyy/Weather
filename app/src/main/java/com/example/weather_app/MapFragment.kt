package com.example.weather_app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.io.IOException

private val FINE_PERMISSION_CODE = 1

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var search: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        search = view.findViewById(R.id.mapSearch)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location: String = search.query.toString()
                var addressList: List<Address>? = null
                if (!location.isNullOrEmpty()) {
                    val geocoder = Geocoder(requireContext())
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address = addressList?.firstOrNull()
                    if (address != null) {
                        val latLng = LatLng(address.latitude, address.longitude)
                        myMap.addMarker(
                            MarkerOptions().position(latLng).title(location)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        )
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Location not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapview) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return view
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return
        }
        val task: Task<Location>? = fusedLocationProviderClient.lastLocation
        task?.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        myMap = p0
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Location Permission is denied,Please allow the permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
