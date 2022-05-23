package org.lsposed.wsa.helper

import android.content.*
import android.os.Handler
import android.util.Log
import dalvik.system.PathClassLoader

private var installReceiver: BroadcastReceiver? = null
const val TAG: String = "LSPosed WSA Helper"

fun getWSAInstallReceiver(context: Context) = runCatching {
    if (installReceiver != null) return@runCatching installReceiver
    val info = context.packageManager.getPackageInfo("com.microsoft.windows.systemapp", 0)
    val cl = PathClassLoader(info.applicationInfo.sourceDir, context.classLoader)
    val handlerClass =
        cl.loadClass("com.microsoft.windows.redirection.AppsRedirectionHandler")
    val ctor = handlerClass.getConstructor(Context::class.java)
    ctor.newInstance(object : ContextWrapper(context) {
        fun onRegister(receiver: BroadcastReceiver?, filter: IntentFilter) {
            Log.d(TAG, "skip receiver")
            if (filter.hasAction(Intent.ACTION_PACKAGE_ADDED) && receiver != null) {
                installReceiver = receiver
                Log.i(TAG, "got install receiver")
            }
        }

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter
        ) = run {
            onRegister(receiver, filter)
            super.registerReceiver(receiver, filter)
        }

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter,
            flags: Int
        ) = run {
            onRegister(receiver, filter)
            super.registerReceiver(receiver, filter, flags)
        }

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter,
            broadcastPermission: String?,
            scheduler: Handler?
        ) = run {
            onRegister(receiver, filter)
            super.registerReceiver(receiver, filter, broadcastPermission, scheduler)
        }

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter,
            broadcastPermission: String?,
            scheduler: Handler?,
            flags: Int
        ) = run {
            onRegister(receiver, filter)
            super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags)
        }
    })
    installReceiver
}.getOrNull()
