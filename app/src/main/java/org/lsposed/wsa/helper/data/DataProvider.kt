package org.lsposed.wsa.helper.data

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import org.lsposed.wsa.helper.data.model.App

object DataProvider {
    private fun isPackageSystemUpdatableOrNonSystem(appInfo: ApplicationInfo): Boolean {
        return appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0 || appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
    }

    private fun PackageManager.isPackageLaunchable(packageName: String): Boolean {
        val launchIntent = Intent("android.intent.action.MAIN", null)
        launchIntent.addCategory("android.intent.category.LAUNCHER")
        launchIntent.setPackage(packageName)
        return resolveActivity(launchIntent, 0) != null
    }

    val hiddenPackages = lazy {
        hashSetOf(
            "com.android.camera2",
            "com.android.contacts",
            "com.android.documentsui",
            "com.android.gallery3d",
            "com.android.settings",
            "com.android.traceur",
        )
    }


    fun getApplicationList(pm: PackageManager) = with(pm) {
        getInstalledPackages(PackageManager.MATCH_DISABLED_COMPONENTS).filter {
            !hiddenPackages.value.contains(it.packageName) && it.applicationInfo != null
        }.filter {
            isPackageSystemUpdatableOrNonSystem(it.applicationInfo) || isPackageLaunchable(it.packageName)
        }.map {
            App(
                label = it.applicationInfo.loadLabel(this),
                packageName = it.packageName,
                versionName = it.versionName,
                versionCode = it.longVersionCode,
                icon = it.applicationInfo.loadIcon(pm),
                applicationInfo = it.applicationInfo,
                selected = false,
            )
        }
    }
}
