package com.example.testsupercom.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class MyLocation(
    @PrimaryKey val id: String,
    val latlng: String,
)