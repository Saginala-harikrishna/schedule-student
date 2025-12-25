package com.example.schedulestudent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlanDao {

    @Insert
    suspend fun insertPlan(plan: Plan)

    @Update
    suspend fun updatePlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    // 🔹 All plans (optional, still useful)
    @Query("SELECT * FROM plans ORDER BY id DESC")
    suspend fun getAllPlans(): List<Plan>

    // 🔹 Pending plans (IMPORTANT)
    @Query("SELECT * FROM plans WHERE isCompleted = 0 ORDER BY id DESC")
    suspend fun getPendingPlans(): List<Plan>

    // 🔹 Completed plans (IMPORTANT)
    @Query("SELECT * FROM plans WHERE isCompleted = 1 ORDER BY id DESC")
    suspend fun getCompletedPlans(): List<Plan>

    // 🔹 Used for Edit flow
    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    suspend fun getPlanById(id: Int): Plan?
}
