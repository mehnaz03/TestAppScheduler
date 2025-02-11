package com.mehnaz.appscheduler.adapters

import android.content.pm.PackageManager
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mehnaz.appscheduler.data.model.Schedule
import com.mehnaz.appscheduler.databinding.ItemScheduleBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleAdapter(private val onItemClick: (Schedule) -> Unit) :
    ListAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder>(ScheduleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScheduleViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.appNameTextView.text = schedule.packageName
            val packageManager = itemView.context.packageManager


            binding.timeTextView.text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(Date(schedule.timeInMillis))


            try {
                val appIcon = packageManager.getApplicationIcon(schedule.packageName)
                binding.appIconImageView.setImageDrawable(appIcon)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }


            binding.executionStatusTextView.text = if (schedule.executed) {
                "Status: Executed"
            } else {
                "Status: Pending"
            }
            binding.executionStatusTextView.setTextColor(
                if (schedule.executed) Color.BLUE else Color.RED
            )


            binding.root.setOnClickListener { onItemClick(schedule) }
        }
    }
}

class ScheduleDiffCallback : DiffUtil.ItemCallback<Schedule>() {
    override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.packageName == newItem.packageName &&
                oldItem.timeInMillis == newItem.timeInMillis &&
                oldItem.executed == newItem.executed  // ✅ Ensure status updates
    }
}
