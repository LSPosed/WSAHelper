package org.lsposed.wsa.helper.data.model

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

data class App(
    val label: CharSequence,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val icon: Drawable,
    val applicationInfo: ApplicationInfo,
    val selected: Boolean
)
