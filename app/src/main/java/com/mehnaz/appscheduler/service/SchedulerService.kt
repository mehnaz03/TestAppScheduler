package com.mehnaz.appscheduler.service

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.mehnaz.appscheduler.data.database.ScheduleDatabase
import com.mehnaz.appscheduler.data.repositories.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SchedulerService : Service() {

    private lateinit var repository: ScheduleRepository

    override fun onCreate() {
        super.onCreate()
        val database = ScheduleDatabase.getDatabase(applicationContext)
        repository = ScheduleRepository(database.scheduleDao())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            Log.e("SchedulerService", "Received null intent, stopping service")
            return START_NOT_STICKY
        }

        val scheduleId = intent?.getIntExtra("SCHEDULE_ID", -1)

        Log.d("SchedulerService", "Alarm triggered for: $packageName, ID: $scheduleId")
        Toast.makeText(this, "Scheduled task executed!", Toast.LENGTH_LONG).show()
        if (packageName.isBlank() || scheduleId == -1) {
            Log.e("SchedulerService", "Invalid package name or schedule ID")
            return START_NOT_STICKY
        }

        Log.d("SchedulerService", "Received intent - Package: $packageName, Schedule ID: $scheduleId")

        if (isAppLaunchable(packageName)) {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(launchIntent)
                    Log.d("SchedulerService", "Launching app: $packageName")
                } catch (e: Exception) {
                    Log.e("SchedulerService", "Error launching app '$packageName': ${e.message}")
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (scheduleId != null) {
                            repository.markExecuted(scheduleId)
                        }
                        Log.d("SchedulerService", "Marked schedule $scheduleId as executed")
                    } catch (e: Exception) {
                        Log.e("SchedulerService", "Error updating execution status: ${e.message}")
                    }
                }
            } else {
                Log.e("SchedulerService", "Launch intent for app '$packageName' is null")
            }
        } else {
            Log.e("SchedulerService", "App '$packageName' is not installed or has no launchable activity.")
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun isAppLaunchable(packageName: String): Boolean {
        return try {
            val packageManager = packageManager
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent != null
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("SchedulerService", "App '$packageName' not found: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("SchedulerService", "Error checking launchable app '$packageName': ${e.message}")
            false
        }
    }
}
