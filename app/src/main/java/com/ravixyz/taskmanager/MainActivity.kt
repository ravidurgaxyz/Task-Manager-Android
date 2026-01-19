package com.ravixyz.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ravixyz.taskmanager.model.SettingsViewModel
import com.ravixyz.taskmanager.ui.theme.TaskManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = viewModel()
            viewModel.readConfig(LocalContext.current)
            TaskManagerTheme(viewModel = viewModel) {
                App(viewModel)
            }
        }
    }
}
