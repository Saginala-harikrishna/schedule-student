package com.example.schedulestudent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "range_targets")
data class RangeTarget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val startDate: String,
    val endDate: String,


    var progress: Int   // ✅ MUST be var
)

