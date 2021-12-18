package org.lsposed.wsa.helper

import android.content.BroadcastReceiver
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val installReceiver: BroadcastReceiver? = getWSAInstallReceiver(this)
        setContent {
            MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors() else lightColors()) {
                if (installReceiver == null || (applicationInfo.flags or ApplicationInfo.FLAG_SYSTEM) == 0) {
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
