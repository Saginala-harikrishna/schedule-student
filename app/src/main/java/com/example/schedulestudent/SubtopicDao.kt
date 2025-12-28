package com.example.schedulestudent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubtopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subtopics: List<SubtopicEntity>)

    @Query("SELECT * FROM subtopics WHERE subtopicsRangeId = :rangeId")
    suspend fun getByRangeId(rangeId: Int): List<SubtopicEntity>

    @Query("SELECT COUNT(*) FROM subtopics WHERE subtopicsRangeId = :rangeId")
    suspend fun getTotalCount(rangeId: Int): Int

    @Query("SELECT COUNT(*) FROM subtopics WHERE subtopicsRangeId = :rangeId AND isCompleted = 1")
    suspend fun getCompletedCount(rangeId: Int): Int

    @Update
    suspend fun update(subtopic: SubtopicEntity)

    @Query("DELETE FROM subtopics WHERE id = :subtopicId")
    suspend fun deleteById(subtopicId: Int)

    @Query("DELETE FROM subtopics WHERE subtopicsRangeId = :rangeId")
    suspend fun deleteByRangeId(rangeId: Int)

    // ✅ THIS FIXES YOUR ERROR
    @Query(
        "UPDATE subtopics SET isCompleted = :isCompleted WHERE id = :subtopicId"
    )
    suspend fun updateCompletion(
        subtopicId: Int,
        isCompleted: Boolean
    )
}
