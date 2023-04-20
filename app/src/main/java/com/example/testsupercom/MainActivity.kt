package com.example.testsupercom

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.testsupercom.databinding.ActivityMainBinding
import com.example.testsupercom.services.MainViewModel
import com.example.testsupercom.ui.BluetoothFragment
import com.example.testsupercom.ui.LocationFragment
import com.example.testsupercom.util.BluetoothReceiver
import com.example.testsupercom.util.InternetConnectivity
import com.example.testsupercom.util.Util
import com.example.testsupercom.util.WifiReceiver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var wifiReceiver: WifiReceiver
    private lateinit var bluetoothReceiver: BluetoothReceiver
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var locationFragment: LocationFragment = LocationFragment.newInstance()
    private var blueToothFragment: BluetoothFragment = BluetoothFragment.newInstance()
    var errorDialogIsShowing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpObservers(this.findViewById(android.R.id.content)/*gets the content view*/)
        checkPermissions()
        wifiReceiver = WifiReceiver()
        bluetoothReceiver = BluetoothReceiver(viewModel)
        setUpView()


    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.BLUETOOTH_SCAN) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun setUpView(){

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_location -> {
                    setFragment(locationFragment,R.id.action_location)
                }
                R.id.action_bluetooth-> {
                    setFragment(blueToothFragment,R.id.action_bluetooth)
                }
            }
            true
        }

        if(viewModel.currentMainFragment!=null){
            binding.bottomNavigationView.selectedItemId = viewModel.currentMainFragment!!
        }else{
            binding.bottomNavigationView.selectedItemId = R.id.action_location
        }
    }



    private fun setUpObservers(view: View){
        val snackBar: Snackbar =
            Snackbar.make(view, "Can't Connect To Web..", Snackbar.LENGTH_INDEFINITE)
                .setAction("GO TO SETTINGS") {
                    this@MainActivity?.let { it1 -> InternetConnectivity.connectToInternet(applicationContext) }
                }
        Util.hasInternet.observe(this, Observer { it ->
            if (!it) {
                snackBar.show()
            } else {
                snackBar.dismiss()
            }
        })

        Util.requestError.observe(this) {
            if (it != 0) {
                if (!errorDialogIsShowing)
                    makeErrorDialog(it)
            }
        }

        viewModel.isSeraching.observe(this){
            if(it){

            }else{

            }
        }
    }

    private fun setFragment(fragment: Fragment, num:Int){
        viewModel.currentMainFragment = num
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_activity_fragment_container,fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val bluetoothFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(wifiReceiver, filter)
        registerReceiver(bluetoothReceiver, bluetoothFilter)
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(wifiReceiver)
        unregisterReceiver(bluetoothReceiver)
    }




    private fun makeErrorDialog(num: Int){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.error_request_dalog, null)
        val checkInternetButton = dialogView.findViewById(R.id.checkInternetButton) as Button
        val yesButton = dialogView.findViewById(R.id.tryAgainButton) as Button

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setView(dialogView).setCancelable(true)

        val dialog = alertDialog.create()
        dialog.show()
        errorDialogIsShowing = true

        yesButton.setOnClickListener {
            dialog.dismiss()
            errorDialogIsShowing = false
        }

        checkInternetButton.setOnClickListener {
            this@MainActivity.let { InternetConnectivity.connectToInternet(applicationContext) }
        }
    }

}