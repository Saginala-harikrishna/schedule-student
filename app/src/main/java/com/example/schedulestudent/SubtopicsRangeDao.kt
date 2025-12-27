package com.example.schedulestudent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubtopicsRangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(range: SubtopicsRangeEntity): Long

    @Query("SELECT * FROM subtopics_range ORDER BY id DESC")
    suspend fun getAll(): List<SubtopicsRangeEntity>
}


