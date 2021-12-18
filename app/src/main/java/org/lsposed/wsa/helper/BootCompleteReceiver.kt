package org.lsposed.wsa.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        getWSAInstallReceiver(context)?.onReceive(
            context,
            Intent(
                Intent.ACTION_PACKAGE_ADDED,
                Uri.parse("package:${context.packageName}")
            ).apply { putExtra(Intent.EXTRA_REPLACING, false) })
    }
}
