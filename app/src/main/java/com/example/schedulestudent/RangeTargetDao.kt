package com.example.schedulestudent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface RangeTargetDao {

    @Insert
    suspend fun insertRangeTarget(target: RangeTarget)

    @Update
    suspend fun updateRangeTarget(target: RangeTarget)

    @Delete
    suspend fun deleteRangeTarget(target: RangeTarget)

    @Query("SELECT * FROM range_targets ORDER BY id DESC")
    suspend fun getAllRangeTargets(): List<RangeTarget>

    // 🔹 REQUIRED FOR EDIT FLOW
    @Query("SELECT * FROM range_targets WHERE id = :id LIMIT 1")
    suspend fun getRangeTargetById(id: Int): RangeTarget?
}
