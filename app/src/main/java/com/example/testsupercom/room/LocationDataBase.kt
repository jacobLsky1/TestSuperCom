package com.example.testsupercom.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.testsupercom.room.LocationDao

@Database(entities = [MyLocation::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun userDao(): LocationDao
}