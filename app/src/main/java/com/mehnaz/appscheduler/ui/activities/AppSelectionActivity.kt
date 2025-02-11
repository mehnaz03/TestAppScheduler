package com.mehnaz.appscheduler.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehnaz.appscheduler.adapters.AppAdapter
import com.mehnaz.appscheduler.utils.AppUtils
import com.mehnaz.appscheduler.databinding.ActivityAppSelectionBinding


class AppSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppSelectionBinding  

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select An App"
        binding.appRecyclerView.layoutManager = LinearLayoutManager(this)

        val installedApps = AppUtils.getInstalledApps(this)
        val adapter = AppAdapter(installedApps) { appInfo ->
            val intent = Intent().apply {
                putExtra("PACKAGE_NAME", appInfo.packageName)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.appRecyclerView.adapter = adapter
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

}

