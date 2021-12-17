package org.lsposed.wsa.helper

import android.content.BroadcastReceiver
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lsposed.wsa.helper.data.DataProvider
import org.lsposed.wsa.helper.data.model.App

fun List<App>.countSelected() = count { it.selected }
fun List<App>.selectedPackages() = filter { it.selected }.map { it.packageName }

@Suppress("FunctionName")
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun HomePageContent(broadcastReceiver: BroadcastReceiver) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf(DataProvider.getApplicationList(context.packageManager)) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showFloatingActionButton by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun BottomSheetScaffoldState.trigger() {
        scope.launch {
            bottomSheetState.apply {
                if (isCollapsed) expand() else collapse()
            }
        }
    }

    fun BottomSheetScaffoldState.collapse() {
        scope.launch {
            bottomSheetState.collapse()
        }
    }

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
            scaffoldState.trigger()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFloatingActionButton,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        scaffoldState.trigger()
                    }) {
                    Icon(Icons.Filled.Settings, null)
                }
            }
        },
        sheetContent = {
            Box(
                modifier = Modifier.height(BottomSheetScaffoldDefaults.SheetPeekHeight)
                    .padding(0.dp)
                    .fillMaxWidth().clickable {
                        showAboutDialog = !showAboutDialog
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    context.applicationInfo.loadLabel(context.packageManager).toString(),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier.height(BottomSheetScaffoldDefaults.SheetPeekHeight)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        broadcast(Intent.ACTION_PACKAGE_ADDED)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Icon",
                        Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Create Icon")
                }
            }
            Box(
                modifier = Modifier.height(BottomSheetScaffoldDefaults.SheetPeekHeight)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        broadcast(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Icon",
                        Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Delete Icon")
                }
            }

        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                bottom = 16.dp + BottomSheetScaffoldDefaults.SheetPeekHeight,
                top = 16.dp,
                start = 8.dp,
                end = 8.dp
            )
        ) {
            items(items = apps) {
                AppListItem(app = it) {
                    apps = apps.map { that ->
                        if (it.packageName == that.packageName) {
                            that.copy(selected = !that.selected)
                        } else that
                    }
                    showFloatingActionButton = apps.countSelected() > 0
                    if (!showFloatingActionButton) {
                        scaffoldState.collapse()
                    }
                }
            }

        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text("WSA Helper")
            },
            text = {
                Text("This app can help you manually create and remove app icons in the start menu")
            },
            buttons = {},
        )
    }
}
