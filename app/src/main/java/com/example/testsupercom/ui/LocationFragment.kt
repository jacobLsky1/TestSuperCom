package com.example.testsupercom.ui

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.testsupercom.R
import com.example.testsupercom.databinding.FragmentBluetoothBinding
import com.example.testsupercom.databinding.FragmentMapBinding
import com.example.testsupercom.services.LocationService
import com.example.testsupercom.services.LocationWorker
import com.example.testsupercom.services.MainViewModel
import com.example.testsupercom.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startService
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LocationFragment : Fragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMapBinding
    private lateinit var workManager: WorkManager
    private var workRequestId: UUID? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        workManager = WorkManager.getInstance(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding = FragmentMapBinding.inflate(layoutInflater)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun setUpView(){
        binding.startTrackingbtn.setOnClickListener {
         Util.isTracking.postValue(true)

            startLocationUpdates()
            val intent = Intent(requireContext(), LocationService::class.java)
            requireContext().startService(intent)
        }

        binding.stopTrackingbtn.setOnClickListener {
            Util.isTracking.postValue(false)
            doAsync {
                stopLocationUpdates()
            }
            val intent = Intent(requireContext(), LocationService::class.java)
            requireContext().stopService(intent)

        }
    }

    private fun startLocationUpdates(){
        val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(30, TimeUnit.SECONDS)
            .build()
        workRequestId = workRequest.id
        workManager.enqueue(workRequest)
        workManager.getWorkInfoByIdLiveData(workRequestId!!).observe(viewLifecycleOwner) { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> {
                    Toast.makeText(requireContext(), "ENQUEUED", Toast.LENGTH_SHORT).show()
                }
                WorkInfo.State.RUNNING -> {
                    Toast.makeText(requireContext(), "RUNNING", Toast.LENGTH_SHORT).show()
                    LocationWorker.locationLiveData.observe(viewLifecycleOwner){
                        doAsync {
                            viewModel.addLocation(it)
                        }
                    }
                }
                WorkInfo.State.SUCCEEDED -> {
                    Toast.makeText(requireContext(), "SUCCEEDED", Toast.LENGTH_SHORT).show()
                    val result = workInfo.outputData.getString("result")
                    viewModel.addLocation(result!!)
                }
                WorkInfo.State.FAILED -> {
                    Toast.makeText(requireContext(), "FAILED", Toast.LENGTH_SHORT).show()
                }
                WorkInfo.State.CANCELLED -> {
                    Toast.makeText(requireContext(), "CANCELLED", Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }
    }

    private fun stopLocationUpdates() {
        workRequestId?.let {
            WorkManager.getInstance(requireContext()).cancelWorkById(it)
            viewModel.clearToTwentyLocations()
        }
    }

    private fun setUpObservers(){
        viewModel.locations.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()){
                googleMap.clear()
                getMyLocation()
                val polylineOptions = PolylineOptions()
                var latLngList = mutableListOf<LatLng>()
                for(i in it){
                    var latlagArr = i.split(' ')
                    val latlng = LatLng(latlagArr[0].toDouble(),latlagArr[1].toDouble())
                    latLngList.add(latlng)
                }
                for (latLng in latLngList) {
                    val markerOptions = MarkerOptions().position(latLng)
                    googleMap.addMarker(markerOptions)
                    polylineOptions.add(latLng)
                }
                googleMap.addPolyline(polylineOptions)
                if (latLngList.isNotEmpty()) {
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngList[0], 14f)
                    googleMap.moveCamera(cameraUpdate)
                }
            }
        }

        Util.isTracking.observe(viewLifecycleOwner){
            if(it){
                binding.startTrackingbtn.isEnabled = false
                binding.stopTrackingbtn.isEnabled  = true
            }else{
                binding.startTrackingbtn.isEnabled = true
                binding.stopTrackingbtn.isEnabled  = false
            }
        }
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setUpView()
        setUpObservers()
        getMyLocation()
        doAsync {
            viewModel.getLastLocations()
        }
    }

    fun getMyLocation(){
        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                googleMap.addMarker(MarkerOptions().position(currentLatLng))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    companion object{
        fun newInstance(): LocationFragment {
            return LocationFragment()
        }
    }
}