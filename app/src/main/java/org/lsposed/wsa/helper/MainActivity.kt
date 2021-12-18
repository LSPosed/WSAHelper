package org.lsposed.wsa.helper

import android.content.*
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import dalvik.system.PathClassLoader

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    private lateinit var installReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            val info = packageManager.getPackageInfo("com.microsoft.windows.systemapp", 0)
            val cl = PathClassLoader(info.applicationInfo.sourceDir, classLoader)
            val handlerClass =
                cl.loadClass("com.microsoft.windows.redirection.AppsRedirectionHandler")
            val ctor = handlerClass.getConstructor(Context::class.java)
            ctor.newInstance(object : ContextWrapper(this) {
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
        }
        setContent {
            MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors() else lightColors()) {
                if (!this@MainActivity::installReceiver.isInitialized || (applicationInfo.flags or ApplicationInfo.FLAG_SYSTEM) == 0) {
                    AlertDialog(
                        onDismissRequest = { finish() },
                        title = {
                            Text("Failed to initialized")
                        },
                        buttons = {}
                    )
                } else {
                    HomePageContent(installReceiver)
                }
            }
        }
    }
}
