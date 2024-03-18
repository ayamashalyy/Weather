package com.example.weather_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.BundleCompat.getParcelable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.Home.view.DayListAdapter
import com.example.weather_app.Home.view.HomeFragment
import com.example.weather_app.Home.view.WeekListAdapter
import com.example.weather_app.Home.viewModel.HomeViewModel
import com.example.weather_app.Home.viewModel.HomeViewModelFactory
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepositoryImp
import com.example.weather_app.dp.WeatherLocalDataSourceImp
import com.example.weather_app.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

open class PlaceCurrentWeather : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var city: TextView
    private lateinit var date: TextView
    private lateinit var dec: TextView
    private lateinit var temp: TextView
    private lateinit var image: ImageView
    private lateinit var humidity: TextView
    private lateinit var clouds: TextView
    private lateinit var pressure: TextView
    private lateinit var wind: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewday: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var hourAdapter: DayListAdapter
    private lateinit var dayAdapter: WeekListAdapter
    var long:Double? =0.0
    var lat :Double? = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        city = view.findViewById(R.id.city_name_txt)
        date = view.findViewById(R.id.date_txt)
        dec = view.findViewById(R.id.temp_desc)
        temp = view.findViewById(R.id.temp_txt)
        image = view.findViewById(R.id.weatherImg)
        humidity = view.findViewById(R.id.humidity_txt)
        clouds = view.findViewById(R.id.clouds_txt)
        pressure = view.findViewById(R.id.pressure_txt)
        wind = view.findViewById(R.id.wind_txt)
        recyclerView = view.findViewById(R.id.hourly_weather_recycler)
        recyclerViewday = view.findViewById(R.id.week_weather_recycler)
        setUpRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle!=null){
            val data = bundle.getParcelable<FavLocation>("cardViewId")
            long = data?.longitude
            lat = data?.latitude
        }

        val homeFactory = HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                WeatherLocalDataSourceImp.getInstance(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, homeFactory).get(HomeViewModel::class.java)
        lifecycleScope.launch {
            viewModel.getMyWeatherStatus(lat!!,long!!,"en","K")
            viewModel._weather.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is ApiState.Success -> {
                        recyclerView.visibility = View.VISIBLE
                        val weather = result.data
                        city.text = weather.city.name
                        hourAdapter.submitList(weather.list)
                        dayAdapter.submitList(weather.list)
                        date.text = weather.list.firstOrNull()?.let {
                            SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(
                                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(it.dt_txt)
                            )
                        }
                        dec.text =
                            weather.list.firstOrNull()?.weather?.firstOrNull()?.description
                                ?: "No description available"
                        temp.text = "${weather.list.firstOrNull()?.main?.temp}°C"
                        Glide.with(this@PlaceCurrentWeather)
                            .load("https://openweathermap.org/img/wn/${weather.list[0].weather[0].icon}@2x.png")
                            .into(image)
                        humidity.text =
                            "${weather.list.firstOrNull()?.main?.humidity.toString()}%"
                        clouds.text = "${weather.list.firstOrNull()?.clouds?.all.toString()}%"
                        pressure.text =
                            "${weather.list.firstOrNull()?.main?.pressure.toString()}hpa"
                        wind.text = "${weather.list.firstOrNull()?.wind?.speed.toString()}m/s"
                    }

                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "Error loading weather data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        hourAdapter = DayListAdapter(requireContext())
        recyclerView.adapter = hourAdapter
        recyclerView.layoutManager = linearLayoutManager

        val weekLinearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        dayAdapter = WeekListAdapter(requireContext())
        recyclerViewday.adapter = dayAdapter
        recyclerViewday.layoutManager = weekLinearLayoutManager
    }
}