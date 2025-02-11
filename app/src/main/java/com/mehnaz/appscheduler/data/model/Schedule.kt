package com.mehnaz.appscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_table")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val timeInMillis: Long,
    var executed: Boolean = false
)
