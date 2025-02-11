package com.mehnaz.appscheduler.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mehnaz.appscheduler.data.model.Schedule

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insert(schedule: Schedule)

    @Query("SELECT * FROM schedule_table")
    fun getAllSchedules(): LiveData<List<Schedule>>

    @Delete
    suspend fun delete(schedule: Schedule)


    @Query("UPDATE schedule_table SET executed = 1 WHERE id = :scheduleId")
    suspend fun markExecuted(scheduleId: Int)


    @Query("SELECT * FROM schedule_table WHERE id = :scheduleId LIMIT 1")
    fun getScheduleById(scheduleId: Int): LiveData<Schedule>


    @Update
    suspend fun update(schedule: Schedule)
}
