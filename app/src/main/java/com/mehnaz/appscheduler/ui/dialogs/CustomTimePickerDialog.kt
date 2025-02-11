package com.mehnaz.appscheduler.ui.dialogs



import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.mehnaz.appscheduler.databinding.DialogTimePickerBinding


class CustomTimePickerDialog(
    private val onTimeSet: (hour: Int, minute: Int, isAm: Boolean) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogTimePickerBinding.inflate(LayoutInflater.from(requireContext()))


        binding.timePicker.setIs24HourView(false)
        binding.timePicker.forceSpinnerMode()

        return AlertDialog.Builder(requireContext())
            .setTitle("Select Time")
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                val hour = binding.timePicker.hour
                val minute = binding.timePicker.minute
                val isAm = hour < 12 // AM if hour is < 12

                onTimeSet(hour, minute, isAm)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun TimePicker.forceSpinnerMode() {
        try {
            val field = TimePicker::class.java.getDeclaredField("mDelegate")
            field.isAccessible = true
            val delegate = field.get(this)
            val spinnerClass = Class.forName("android.widget.TimePickerSpinnerDelegate")
            if (delegate?.javaClass != spinnerClass) {
                field.set(this, null)
                this.setIs24HourView(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
