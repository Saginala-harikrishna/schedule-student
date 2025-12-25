package com.example.schedulestudent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "range_targets")
data class RangeTarget(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    // Store dates as String for simplicity (dd/MM/yyyy)
    val startDate: String,
    val endDate: String,

    // Progress between 0–100
    var progress: Int
)
