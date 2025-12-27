package com.example.schedulestudent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtopics_range")
data class SubtopicsRangeEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val startDate: String,
    val endDate: String
)
