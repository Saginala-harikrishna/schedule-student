package com.example.schedulestudent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class Plan(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    var title: String,

    var date: String,

    var isCompleted: Boolean = false
)
