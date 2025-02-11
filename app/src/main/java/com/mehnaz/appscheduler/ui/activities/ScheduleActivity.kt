package com.mehnaz.appscheduler.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mehnaz.appscheduler.R
import com.mehnaz.appscheduler.data.model.Schedule
import com.mehnaz.appscheduler.utils.ScheduleUtil
import com.mehnaz.appscheduler.ui.viewmodel.ScheduleViewModel
import com.mehnaz.appscheduler.databinding.ScheduleActivityBinding
import com.mehnaz.appscheduler.ui.dialogs.CustomTimePickerDialog
import java.util.Calendar

class ScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ScheduleActivityBinding
    private lateinit var viewModel: ScheduleViewModel
    private var packageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScheduleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val scheduleId = intent.getIntExtra("scheduleId", -1)
        if (scheduleId != -1) {
            supportActionBar?.title = "Edit Schedule"

            viewModel.getScheduleById(scheduleId).observe(this) { schedule ->
                if (schedule != null) {
                    packageName = schedule.packageName
                    binding.selectedAppText.text = packageName

                    try {
                        val appIcon = packageManager.getApplicationIcon(packageName!!)
                        binding.selectedAppIcon.setImageDrawable(appIcon)
                        binding.selectedAppIcon.visibility = View.VISIBLE
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }

                    binding.selectAppText.text = "Edit your app schedule"

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = schedule.timeInMillis

                    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val amPm = if (hourOfDay < 12) "AM" else "PM"
                    val hour12 = if (hourOfDay == 0) 12 else if (hourOfDay > 12) hourOfDay - 12 else hourOfDay

                    val timeString = String.format("%02d:%02d %s", hour12, minute, amPm)
                    binding.timePickerButton.text = timeString
                } else {
                    Log.e("ScheduleActivity", "Schedule not found with ID: $scheduleId")
                }
            }

            binding.selectAppButton.visibility = View.GONE
        } else {
            supportActionBar?.title = "Make App Schedule"
        }

        binding.selectAppButton.setOnClickListener {
            val intent = Intent(this, AppSelectionActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.timePickerButton.setOnClickListener {
            CustomTimePickerDialog { hour, minute, isAm ->
                val amPm = if (isAm) "AM" else "PM"
                val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                binding.timePickerButton.text = String.format("%02d:%02d %s", formattedHour, minute, amPm)
            }.show(supportFragmentManager, "TimePickerDialog")
        }

        binding.saveScheduleButton.setOnClickListener {
            val timeText = binding.timePickerButton.text.toString()
            val timeParts = timeText.split(":")

            if (timeParts.size == 2) {
                val hourStr = timeParts[0].trim()
                val minuteParts = timeParts[1].trim().split(" ")
                if (minuteParts.size < 2) {
                    Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val minuteStr = minuteParts[0]
                val isAm = minuteParts[1].equals("AM", ignoreCase = true)

                var hour = hourStr.toIntOrNull() ?: 0
                if (!isAm && hour < 12) {
                    hour += 12
                } else if (isAm && hour == 12) {
                    hour = 0
                }

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minuteStr.toIntOrNull() ?: 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (packageName == null) {
                    Toast.makeText(this, "Please select an app", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val schedule = Schedule(
                    id = if (scheduleId != -1) scheduleId else 0,
                    packageName = packageName!!,
                    timeInMillis = calendar.timeInMillis
                )

                if (scheduleId != -1) {
                    viewModel.update(schedule)
                } else {
                    viewModel.insert(schedule)
                }

                ScheduleUtil.scheduleAppLaunch(this, schedule)
                finish()
            } else {
                Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            packageName = data?.getStringExtra("PACKAGE_NAME")
            packageName?.let { pkg ->
                try {
                    val appInfo = packageManager.getApplicationInfo(pkg, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val appIcon = packageManager.getApplicationIcon(pkg)

                    binding.selectedAppText.text = appName
                    binding.selectedAppIcon.setImageDrawable(appIcon)
                    binding.selectedAppIcon.visibility = View.VISIBLE
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}

