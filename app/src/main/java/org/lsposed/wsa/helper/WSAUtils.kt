package org.lsposed.wsa.helper

import android.content.*
import android.os.Handler
import android.util.Log
import dalvik.system.PathClassLoader

private var installReceiver: BroadcastReceiver? = null

fun getWSAInstallReceiver(context: Context) = runCatching {
    if (installReceiver != null) return@runCatching installReceiver
    val info = context.packageManager.getPackageInfo("com.microsoft.windows.systemapp", 0)
    val cl = PathClassLoader(info.applicationInfo.sourceDir, context.classLoader)
    val handlerClass =
        cl.loadClass("com.microsoft.windows.redirection.AppsRedirectionHandler")
    val ctor = handlerClass.getConstructor(Context::class.java)
    ctor.newInstance(object : ContextWrapper(context) {
        fun onRegister(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? {
            Log.d("LSPosed", "skip receiver")
            if (filter.hasAction(Intent.ACTION_PACKAGE_ADDED) && receiver != null) {
                installReceiver = receiver
                Log.i("LSPosed", "got install receiver")
            }
            return null
        }

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter
        ) = onRegister(receiver, filter)

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter,
            flags: Int
        ) = onRegister(receiver, filter)

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter,
            broadcastPermission: String?,
            scheduler: Handler?
        ) = onRegister(receiver, filter)

        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter,
            broadcastPermission: String?,
            scheduler: Handler?,
            flags: Int
        ) = onRegister(receiver, filter)
    })
    installReceiver
}.getOrNull()
