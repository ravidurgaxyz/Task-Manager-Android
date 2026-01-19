package com.ravixyz.taskmanager.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ravixyz.taskmanager.model.ColorTheme
import com.ravixyz.taskmanager.model.SettingsViewModel

@Composable
fun Settings(viewModel: SettingsViewModel){

    val config = viewModel.config.collectAsState()
    val context = LocalContext.current
    val packageManager = context.packageManager
    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
    var showColorThemeDialog by remember { mutableStateOf(false) }
    var showImportDataDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    val exportLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            if (uri != null) {
                viewModel.exportData(context, uri)
                Toast.makeText(
                    context,
                    "Exported",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val importLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                viewModel.importFromZip(context, uri)
                Toast.makeText(
                    context,
                    "Imported",
                    Toast.LENGTH_SHORT
                ).show()
                intent?.let {
                    val restartIntent = Intent.makeRestartActivityTask(it.component)
                    context.startActivity(restartIntent)
                    Runtime.getRuntime().exit(0)
                }
            }
        }

    if (showColorThemeDialog){
        Dialog(
            onDismissRequest = { showColorThemeDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ){
                Text(
                    text = "Color Theme",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                )
                Column(
                    Modifier
                        .selectableGroup()
                ) {
                    ColorTheme.entries.forEach { colorTheme ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (colorTheme == config.value.colorTheme),
                                    onClick = {
                                        viewModel.setConfig(
                                            context,
                                            config.value.copy(colorTheme = colorTheme)
                                        )
                                        showColorThemeDialog = false
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (colorTheme == config.value.colorTheme),
                                onClick = null
                            )
                            Text(
                                text = colorTheme.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showImportDataDialog){
        AlertDialog(
            title = {
                Text("Import Data")
            },
            text = {
                Text("If you import data from a zip, all of your current data will be cleared. Importing is only intended to use in the case of uninstalling and reinstalling.")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning"
                )
            },
            onDismissRequest = { showImportDataDialog = false },
            dismissButton = {
                TextButton(onClick = {
                    showImportDataDialog = false
                }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    importLauncher.launch(arrayOf("application/zip"))
                    showImportDataDialog = false
                }) {
                    Text("Import")
                }
            }
        )
    }

    if (showClearDataDialog){
        AlertDialog(
            title = {
                Text("Clear Data")
            },
            text = {
                Text("Do you clear all your data?")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning"
                )
            },
            onDismissRequest = { showClearDataDialog = false },
            dismissButton = {
                TextButton(onClick = {
                    showClearDataDialog = false
                }) {
                    Text("No")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData(context)
                    Toast.makeText(
                        context,
                        "Cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                    showClearDataDialog = false
                }) {
                    Text("Yes")
                }
            }
        )
    }

    LazyColumn {
        item {
            SettingsItem(
                title = "Color Theme",
                value = config.value.colorTheme.toString()
            ) { showColorThemeDialog = true }
        }
        item {
            SettingsItem("Import Data", "Imports from a zip file") {
                showImportDataDialog = true
            }
        }
        item {
            SettingsItem("Export Data", "Exports to a zip file") {
                exportLauncher.launch(null)
            }
        }
        item {
            SettingsItem("Clear", "Clears all of the data") {
                showClearDataDialog = true
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    value: String,
    onClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable( onClick = {
                onClick()
            })
            .padding(top = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        Text(
            text = value,
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}