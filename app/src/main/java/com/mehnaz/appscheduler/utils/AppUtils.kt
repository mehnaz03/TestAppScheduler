package com.mehnaz.appscheduler.utils

import android.content.Context
import android.content.pm.PackageManager
import com.mehnaz.appscheduler.data.model.AppInfo

object AppUtils {
    fun getInstalledApps(context: Context): List<AppInfo> {
        val packageManager = context.packageManager
        val installedApps = mutableListOf<AppInfo>()

        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            val appName = packageInfo.loadLabel(packageManager).toString()
            val packageName = packageInfo.packageName
            val appIcon = packageInfo.loadIcon(packageManager)
            installedApps.add(AppInfo(appName, packageName, appIcon))
        }
        return installedApps.sortedBy { it.appName }
    }
}
