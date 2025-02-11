package com.mehnaz.appscheduler.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager

import android.provider.Settings
import android.util.Log
import android.widget.Toast

import com.mehnaz.appscheduler.data.model.Schedule

import com.mehnaz.appscheduler.service.SchedulerService


object ScheduleUtil {

    fun scheduleAppLaunch(context: Context, schedule: Schedule) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (context.checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                Log.e("ScheduleUtil", "Exact alarm permission not granted.")
                requestPermission(context)
                return
            }
        }
        if (!isBatteryOptimizationIgnored(context)) {
            Log.e("ScheduleUtil", "Battery optimization may delay or cancel the alarm.")
            Toast.makeText(context, "Disable battery optimization for better scheduling", Toast.LENGTH_LONG).show()
        }
        val intent = Intent(context, SchedulerService::class.java).apply {
            putExtra("PACKAGE_NAME", schedule.packageName)
            putExtra("SCHEDULE_ID", schedule.id)
        }

        val requestCode = schedule.id.hashCode()
        val pendingIntent = PendingIntent.getService(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Use setExactAndAllowWhileIdle for better reliability
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, schedule.timeInMillis, pendingIntent)

        Log.d("ScheduleUtil", "Scheduled app launch for ${schedule.packageName} at ${schedule.timeInMillis}")
    }

    fun cancelSchedule(context: Context, schedule: Schedule) {
        val intent = Intent(context, SchedulerService::class.java)
        val requestCode = schedule.id.hashCode()
        val pendingIntent = PendingIntent.getService(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        Log.d("ScheduleUtil", "Cancelled schedule for ${schedule.packageName}")
    }

    private fun requestPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Permission required to set exact alarms", Toast.LENGTH_LONG).show()
                Log.e("ScheduleUtil", "Failed to request permission: ${e.message}")
            }
        }
    }

    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }
}







