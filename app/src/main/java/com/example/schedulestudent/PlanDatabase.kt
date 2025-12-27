package com.example.schedulestudent

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Plan::class,
        RangeTarget::class,
        SubtopicsRangeEntity::class,
        SubtopicEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PlanDatabase : RoomDatabase() {

    // FIXED: Correct DAO name
    abstract fun planDao(): PlanDao
    abstract fun rangeTargetDao(): RangeTargetDao

    // New feature DAOs
    abstract fun subtopicsRangeDao(): SubtopicsRangeDao
    abstract fun subtopicDao(): SubtopicDao

    companion object {

        @Volatile
        private var INSTANCE: PlanDatabase? = null

        fun getDatabase(context: Context): PlanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlanDatabase::class.java,
                    "plan_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
