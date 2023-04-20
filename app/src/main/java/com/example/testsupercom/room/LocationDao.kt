package com.example.testsupercom.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.testsupercom.room.MyLocation

@Dao
interface  LocationDao {
    @Insert
    fun insert(location: MyLocation)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): List<MyLocation>

    @Query("DELETE FROM locations")
    fun deleteAllLocations()
}