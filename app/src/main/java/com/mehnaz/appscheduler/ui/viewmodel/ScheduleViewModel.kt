package com.mehnaz.appscheduler.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mehnaz.appscheduler.data.model.Schedule
import com.mehnaz.appscheduler.data.repositories.ScheduleRepository
import com.mehnaz.appscheduler.data.database.ScheduleDatabase
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScheduleRepository
    var allSchedules: LiveData<List<Schedule>>

    init {
        val dao = ScheduleDatabase.getDatabase(application).scheduleDao()
        repository = ScheduleRepository(dao)
        allSchedules = repository.allSchedules
    }

    // Insert a new schedule
    fun insert(schedule: Schedule) = viewModelScope.launch {
        repository.insert(schedule)
    }

    // Delete a schedule
    fun delete(schedule: Schedule) = viewModelScope.launch {
        repository.delete(schedule)
    }


    // Fetch a schedule by ID for editing
    fun getScheduleById(scheduleId: Int): LiveData<Schedule> {
        return repository.getScheduleById(scheduleId)
    }


    fun update(schedule: Schedule) = viewModelScope.launch {
        repository.update(schedule)
    }
}

