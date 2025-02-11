package com.mehnaz.appscheduler.data.repositories

import androidx.lifecycle.LiveData
import com.mehnaz.appscheduler.data.model.Schedule
import com.mehnaz.appscheduler.data.database.dao.ScheduleDao

class ScheduleRepository(private val scheduleDao: ScheduleDao) {


    val allSchedules: LiveData<List<Schedule>> = scheduleDao.getAllSchedules()


    suspend fun insert(schedule: Schedule) {
        scheduleDao.insert(schedule)
    }


    suspend fun delete(schedule: Schedule) {
        scheduleDao.delete(schedule)
    }


    suspend fun markExecuted(scheduleId: Int) {
        scheduleDao.markExecuted(scheduleId)
    }


    fun getScheduleById(scheduleId: Int): LiveData<Schedule> {
        return scheduleDao.getScheduleById(scheduleId)
    }


    suspend fun update(schedule: Schedule) {
        scheduleDao.update(schedule)
    }
}


