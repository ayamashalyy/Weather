package com.example.weather_app.FavoriteWeather.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.weather_app.FavoriteWeather.viewModel.FavWeatherViewModel
import com.example.weather_app.FavoriteWeather.viewModel.FavWeatherViewModelFactory
import com.example.weather_app.Map.view.MapFragment
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepositoryImp
import com.example.weather_app.PlaceCurrentWeather
import com.example.weather_app.R
import com.example.weather_app.dp.WeatherLocalDataSourceImp
import com.example.weather_app.network.WeatherRemoteDataSourceImp
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavoritesFragment : Fragment(),OnFavoriteClickListener {
    private lateinit var btn: FloatingActionButton
    private lateinit var favFactory: FavWeatherViewModelFactory
    private lateinit var viewModel: FavWeatherViewModel
    private lateinit var favRecycler: RecyclerView
    private lateinit var favAdapter: favListAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var animation:LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        animation = view.findViewById(R.id.fav_Lottie)
        favFactory = FavWeatherViewModelFactory (WeatherRepositoryImp.getInstance(
            WeatherRemoteDataSourceImp.getInstance(),
            WeatherLocalDataSourceImp.getInstance(requireContext())
        ))
        viewModel = ViewModelProvider (this, favFactory).get(FavWeatherViewModel::class.java)
        favRecycler = view.findViewById(R.id.map_rv)
        setUpRecyclerView()
        viewModel.weather.observe(requireActivity()) { weather ->
            if (weather != null) {
                if (weather.size == 0) {
                    animation.visibility = View.VISIBLE
                    favRecycler.visibility = View.GONE
                } else {
                    animation.visibility = View.INVISIBLE
                    favRecycler.visibility = View.VISIBLE
                    favAdapter.submitList(weather)


                }
            }
        }

        btn = view.findViewById(R.id.favBtn)
        btn.setOnClickListener {
            val fragment = MapFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }
    private fun setUpRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        favAdapter = favListAdapter (requireContext(), this)
        favRecycler.adapter = favAdapter
        favRecycler.layoutManager = layoutManager
    }

    override fun deleteLocations(weather: FavLocation) {
        viewModel.deleteLocations(weather)
        Toast.makeText(requireContext(),"Deleted Weather", Toast.LENGTH_SHORT).show()
    }

    override fun getWeatherOfFavoriteLocation(favLocation: FavLocation) {
        val fragment = PlaceCurrentWeather().apply {
            arguments = Bundle().apply {
                putParcelable("cardViewId",favLocation)
            }
        }
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()    }


}