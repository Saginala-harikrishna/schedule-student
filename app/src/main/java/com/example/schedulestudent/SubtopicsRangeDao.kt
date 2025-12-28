package com.example.schedulestudent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update   // ✅ REQUIRED import

@Dao
interface SubtopicsRangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(range: SubtopicsRangeEntity): Long

    @Query("SELECT * FROM subtopics_range ORDER BY id DESC")
    suspend fun getAll(): List<SubtopicsRangeEntity>

    // ✅ REQUIRED for edit mode
    @Query("SELECT * FROM subtopics_range WHERE id = :rangeId")
    suspend fun getById(rangeId: Int): SubtopicsRangeEntity

    @Query("DELETE FROM subtopics_range WHERE id = :rangeId")
    suspend fun deleteById(rangeId: Int)

    // ✅ REQUIRED for edit mode
    @Update
    suspend fun update(entity: SubtopicsRangeEntity)
}
