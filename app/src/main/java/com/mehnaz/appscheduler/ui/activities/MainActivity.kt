package com.mehnaz.appscheduler.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehnaz.appscheduler.data.model.Schedule
import com.mehnaz.appscheduler.adapters.ScheduleAdapter
import com.mehnaz.appscheduler.utils.ScheduleUtil
import com.mehnaz.appscheduler.ui.viewmodel.ScheduleViewModel
import com.mehnaz.appscheduler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var adapter: ScheduleAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(ScheduleViewModel::class.java)

        adapter = ScheduleAdapter { schedule -> showOptions(schedule) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.allSchedules.observe(this) { schedules ->
            adapter.submitList(schedules)


            //adapter.notifyDataSetChanged()


            binding.noDataTextView.visibility = if (schedules.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (schedules.isNotEmpty()) View.VISIBLE else View.GONE
        }


        binding.addScheduleButton.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }
    }

    private fun showOptions(schedule: Schedule) {
        val options = arrayOf("Edit", "Delete", "Cancel")
        AlertDialog.Builder(this)
            .setTitle("Manage Schedule")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editSchedule(schedule)
                    1 -> viewModel.delete(schedule)
                    2 -> cancelSchedule(schedule)
                }
            }.show()
    }

    private fun editSchedule(schedule: Schedule) {
        val intent = Intent(this, ScheduleActivity::class.java).apply {
            putExtra("scheduleId", schedule.id)
        }
        startActivity(intent)
    }

    private fun cancelSchedule(schedule: Schedule) {
        ScheduleUtil.cancelSchedule(this, schedule)
        viewModel.delete(schedule)
    }
}

