package com.example.testsupercom.util

import androidx.lifecycle.MutableLiveData

class Util {
    companion object{
        var hasInternet: MutableLiveData<Boolean> = MutableLiveData()
        var requestError:MutableLiveData<Int> = MutableLiveData(0)
        var isTracking : MutableLiveData<Boolean> = MutableLiveData(false)
    }
}