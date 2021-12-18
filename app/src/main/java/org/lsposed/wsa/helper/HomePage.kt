package org.lsposed.wsa.helper

import android.content.BroadcastReceiver
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lsposed.wsa.helper.data.DataProvider
import org.lsposed.wsa.helper.data.model.App

fun List<App>.countSelected() = count { it.selected }
fun List<App>.selectedPackages() = filter { it.selected }.map { it.packageName }

@ExperimentalMaterial3Api
@Suppress("FunctionName")
@ExperimentalAnimationApi
@Composable
fun HomePageContent(broadcastReceiver: BroadcastReceiver) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf(DataProvider.getApplicationList(context.packageManager)) }

    fun broadcast(action: String) {
        CoroutineScope(Dispatchers.Main).launch {
            apps.selectedPackages().forEach {
                broadcastReceiver.onReceive(
                    context,
                    Intent(
                        action,
                        Uri.parse("package:$it")
                    ).apply { putExtra(Intent.EXTRA_REPLACING, false) })
            }
            apps = apps.map { that ->
                if (that.selected) {
                    that.copy(selected = !that.selected)
                } else that
            }
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = apps.countSelected() > 0,
                exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }),
                enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight })
            ) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        label = { Text("Create Icon") },
                        selected = false,
                        onClick = { broadcast(Intent.ACTION_PACKAGE_ADDED) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                        label = { Text("Remove Icon") },
                        selected = false,
                        onClick = { broadcast(Intent.ACTION_PACKAGE_FULLY_REMOVED) }
                    )
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            items(items = apps) {
                AppListItem(app = it) {
                    apps = apps.map { that ->
                        if (it.packageName == that.packageName) {
                            that.copy(selected = !that.selected)
                        } else that
                    }
                }
            }

        }
    }
}
