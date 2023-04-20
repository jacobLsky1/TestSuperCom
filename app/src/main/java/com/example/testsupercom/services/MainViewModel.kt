package com.example.testsupercom.services

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testsupercom.room.MyLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val context: Context, private val repository: MainRepository
): ViewModel() {


    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var currentMainFragment :Int? = null
    var orgList = listOf<String>()

    private var _devices :MutableLiveData<List<String>> = MutableLiveData(listOf())
    var devices : LiveData<List<String>> = _devices

    private var _locations :MutableLiveData<List<String>> = MutableLiveData()
    var locations : LiveData<List<String>> = _locations

    private var _isSeraching :MutableLiveData<Boolean> = MutableLiveData(false)
    var isSeraching : LiveData<Boolean> = _isSeraching


    fun setFilterList(text:String){
        val ornList =
        if(text==""){
            _devices.postValue(orgList)
        }else{
            var list = devices.value?.filter { it.contains(text) }
            if(list==null){
                list = listOf()
            }
            _devices.postValue(list!!)
        }
    }

    fun searchForDevices() {

        _isSeraching.postValue(true)
        _devices.postValue(listOf())

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "no permission to scan", Toast.LENGTH_SHORT).show()
        }
        bluetoothAdapter?.startDiscovery()
    }

    fun addDevice(name:String){
        val list = _devices.value!!.toMutableList()
        list!!.add(name)
        _devices.postValue(list)
        orgList = list

        _isSeraching.postValue(false)
    }

    fun cancelDiscovery(){
        _isSeraching.postValue(false)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }



    fun addLocation(latlng:String){
        repository.insertLocation(MyLocation(UUID.randomUUID().toString(),latlng))
        getLastLocations()
    }

    fun getLastLocations(){
        val locations = repository.getAllLocations()
        var listOfLoc = mutableListOf<String>()
        for(loc in locations){
            listOfLoc.add((loc.latlng))
        }
        _locations.postValue(listOfLoc)
    }

    fun clearToTwentyLocations(){
        val locations = repository.getAllLocations()
        repository.deleteAllLocations()
        if(locations.size>20){
            var sublocations = locations.subList(locations.size-20,locations.size)
            for(loc in sublocations){
                repository.insertLocation(loc)
            }
        }
    }
}