package com.example.weather_app.alert.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.WeatherRepositoryImp
import com.example.weather_app.alert.viewModel.AlertViewModel
import com.example.weather_app.alert.viewModel.AlertViewModelFactory
import com.example.weather_app.databinding.FragmentAlartsBinding
import com.example.weather_app.dp.WeatherLocalDataSourceImp
import com.example.weather_app.network.WeatherRemoteDataSourceImp


class AlertsFragment : Fragment() {
    lateinit var alertDialog: AlertDialog
    private lateinit var binding: FragmentAlartsBinding
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var alertAdapter: AlertListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlartsBinding.inflate(inflater, container, false)
        binding.floatingAlarmActionButton.setOnClickListener {
            alertDialog = AlertDialog(alertViewModel)
            activity?.supportFragmentManager?.let { manger -> alertDialog.show(manger, "dialog") }
        }

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.alertRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        alertAdapter = AlertListAdapter(requireContext(), object : OnClickListener {
            override fun removeFromAlert(alert: AlertModel) {
                alertViewModel.removeAlert(alert)

            }
        })
        binding.alertRv.adapter = alertAdapter

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        alertViewModelFactory = AlertViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                WeatherLocalDataSourceImp.getInstance(requireContext())
            )
        )

        alertViewModel = ViewModelProvider(this, alertViewModelFactory)[AlertViewModel::class.java]
        alertViewModel.alert.observe(viewLifecycleOwner) { alert ->
            if (alert != null) {
                if (alert.size == 0) {
                    binding.alertSplashLottie.visibility = View.VISIBLE
                    binding.alertRv.visibility = View.GONE
                } else {
                    binding.alertSplashLottie.visibility = View.INVISIBLE
                    binding.alertRv.visibility = View.VISIBLE
                    alertAdapter.submitList(alert)

                }
            }

        }

    }
}