package com.ravixyz.taskmanager

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ravixyz.taskmanager.model.SettingsViewModel
import com.ravixyz.taskmanager.ui.Settings
import com.ravixyz.taskmanager.ui.TopBar
import com.ravixyz.taskmanager.ui.task.TaskManager

@Composable
fun App(
    viewModel: SettingsViewModel
){
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "Task Manager"

    Scaffold(
        topBar = {
            TopBar(
                currentRoute,
                backAction = { navController.popBackStack() },
                navigate = { navController.navigate(route = "Settings")}
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "Task Manager",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("Task Manager") {
                TaskManager()
            }
            composable("Settings") {
                Settings(viewModel)
            }
        }
    }
}