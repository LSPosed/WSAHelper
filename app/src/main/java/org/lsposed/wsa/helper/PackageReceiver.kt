package org.lsposed.wsa.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        getWSAInstallReceiver(context)?.onReceive(context, intent)
    }
}
