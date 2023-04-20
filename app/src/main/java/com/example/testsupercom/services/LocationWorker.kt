package com.example.testsupercom.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.google.android.gms.common.ConnectionResult.TIMEOUT
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class LocationWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val request =
            LocationRequest().apply { priority = LocationRequest.PRIORITY_HIGH_ACCURACY }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Result.failure()
        }
        val loc = fusedLocationClient.lastLocation.await()
        if (loc == null) {
            if (runAttemptCount < MAX_ATTEMPT) { // max_attempt = 3
                Result.retry()
            } else {
                Result.failure()
            }
        } else {
            Log.d(TAG, "doWork success $loc")
            locationLiveData.postValue("${loc.latitude} ${loc.longitude}")
            Result.success()
        }
    }

    companion object {
        val TAG = LocationWorker::class.java.simpleName
        const val MAX_ATTEMPT = 3
        var locationLiveData : MutableLiveData<String> = MutableLiveData()
    }
}

