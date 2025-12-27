package com.example.schedulestudent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubtopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subtopics: List<SubtopicEntity>)

    @Query("SELECT * FROM subtopics WHERE subtopicsRangeId = :rangeId")
    suspend fun getByRangeId(rangeId: Int): List<SubtopicEntity>

    @Query("""
        UPDATE subtopics 
        SET isCompleted = :isCompleted 
        WHERE id = :subtopicId
    """)
    suspend fun updateCompletion(subtopicId: Int, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM subtopics WHERE subtopicsRangeId = :rangeId")
    suspend fun getTotalCount(rangeId: Int): Int

    @Query("""
        SELECT COUNT(*) FROM subtopics 
        WHERE subtopicsRangeId = :rangeId 
        AND isCompleted = 1
    """)
    suspend fun getCompletedCount(rangeId: Int): Int
}
