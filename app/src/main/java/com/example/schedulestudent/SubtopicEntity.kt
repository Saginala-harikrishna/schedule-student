package com.example.schedulestudent

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subtopics",
    indices = [Index(value = ["subtopicsRangeId"])],
    foreignKeys = [
        ForeignKey(
            entity = SubtopicsRangeEntity::class,
            parentColumns = ["id"],
            childColumns = ["subtopicsRangeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubtopicEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val subtopicsRangeId: Int,
    val title: String,
    val isCompleted: Boolean = false
)
