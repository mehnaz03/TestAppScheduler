package com.mehnaz.appscheduler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehnaz.appscheduler.data.model.AppInfo
import com.mehnaz.appscheduler.databinding.ItemAppBinding

class AppAdapter(
    private val appList: List<AppInfo>,
    private val onAppSelected: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    inner class AppViewHolder(private val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appInfo: AppInfo) {
            binding.appName.text = appInfo.appName
            binding.appIcon.setImageDrawable(appInfo.appIcon)
            binding.root.setOnClickListener { onAppSelected(appInfo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    override fun getItemCount(): Int = appList.size
}
