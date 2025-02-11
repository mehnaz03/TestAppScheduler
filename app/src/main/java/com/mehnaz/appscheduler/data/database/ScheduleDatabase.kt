package com.mehnaz.appscheduler.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mehnaz.appscheduler.data.model.Schedule
import com.mehnaz.appscheduler.data.database.dao.ScheduleDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized




@Database(entities = [Schedule::class], version = 3, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): ScheduleDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                ) .fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
