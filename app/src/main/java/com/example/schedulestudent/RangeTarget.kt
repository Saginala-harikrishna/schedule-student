package com.example.schedulestudent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "range_targets")
data class RangeTarget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val startDate: Long,
    val endDate: Long,

    var progress: Int   // ✅ MUST be var
)

