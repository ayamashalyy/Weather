package com.example.weather_app.Map.view

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.weather_app.Home.view.HomeFragment
import com.example.weather_app.R
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
import java.util.Locale

private const val FINE_PERMISSION_CODE = 1

class MapSettings : Fragment(), OnMapReadyCallback {

    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var search: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        search = view.findViewById(R.id.mapSearch)
        search.visibility = View.GONE
        sharedPreferences = requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapview) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()

        myMap.setOnMapClickListener { latLng ->
            showSaveDialog(latLng)
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
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
                val latLng = LatLng(location.latitude, location.longitude)
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses: MutableList<Address>? =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val locationName = addresses?.get(0)?.getAddressLine(0)

                myMap.addMarker(
                    MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 2f))
                locationName?.let {
                    saveLocationToSharedPreferences(latLng, it)
                }
            } else {
                Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveLocationToSharedPreferences(location: LatLng, locationName: String?) {
        val editor = sharedPreferences.edit()
        Log.i("Trace Geolocation", "name: ${locationName} ==> lat: ${location.latitude} ==> lan: ${location.longitude}")

        editor.putString("latitude", location.latitude.toString())
        editor.putString("longitude", location.longitude.toString())
        editor.putString("locationName", locationName)
        editor.apply()
    }


    private fun showSaveDialog(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: MutableList<Address>? =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val locationName = addresses?.get(0)?.getAddressLine(0)

        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Save Location")
        alertDialog.setMessage("Do you want to save this location as \"$locationName\"?")
        alertDialog.setPositiveButton("Save") { dialog, which ->
            saveLocationToSharedPreferences(latLng, locationName)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            Toast.makeText(requireContext(), "Location saved successfully", Toast.LENGTH_SHORT)
                .show()
            myMap.clear()
            myMap.addMarker(
                MarkerOptions().position(latLng).title(locationName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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
