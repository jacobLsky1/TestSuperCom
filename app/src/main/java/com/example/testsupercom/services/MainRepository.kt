package com.example.testsupercom.services

import com.example.testsupercom.room.LocationDao
import com.example.testsupercom.room.MyLocation

class MainRepository(val userDao: LocationDao) {
    fun insertLocation(location: MyLocation) {
        userDao.insert(location)
    }

    fun getAllLocations(): List<MyLocation> {
        return userDao.getAllLocations()
    }

    fun deleteAllLocations() {
        userDao.deleteAllLocations()
    }
}