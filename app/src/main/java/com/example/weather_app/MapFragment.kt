package com.example.weather_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() , OnMapReadyCallback {
    lateinit var myMap : GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapview) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        return view
    }

    override fun onMapReady(p0: GoogleMap) {
        myMap = p0
        val alex = LatLng(31.1991497, 29.9081516)
        myMap.addMarker(MarkerOptions().position(alex).title("Alexandria"))
        myMap.moveCamera(CameraUpdateFactory.newLatLng(alex))
    }
    }

