package org.lsposed.wsa.helper

import android.content.BroadcastReceiver
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isSystemInDarkTheme())
                    dynamicDarkColorScheme(this)
                else dynamicLightColorScheme(this)
            } else {
                if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            }
            MaterialTheme(
                colorScheme = colorScheme
            ) {
                val installReceiver: BroadcastReceiver? = getWSAInstallReceiver(this)
                if (installReceiver == null || (applicationInfo.flags or ApplicationInfo.FLAG_SYSTEM) == 0) {
                    AlertDialog(
                        onDismissRequest = { finish() },
                        title = {
                            Text("Failed to initialized\ninstallReceiver=$installReceiver")
                        },
                        confirmButton = {}
                    )
                } else {
                    HomePageContent(installReceiver)
                }
            }
        }
    }
}
