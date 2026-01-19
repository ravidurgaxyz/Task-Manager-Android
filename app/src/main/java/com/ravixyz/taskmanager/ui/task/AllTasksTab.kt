package com.ravixyz.taskmanager.ui.task

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ravixyz.taskmanager.model.TaskManagerViewModel
import com.ravixyz.taskmanager.ui.MiniTaskCard
import java.time.LocalDate

@Composable
fun TasksTab(
    viewModel: TaskManagerViewModel
){
    val todayTasks = viewModel.todayTasks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {

        items(todayTasks.value){
            MiniTaskCard(
                task = it,
                deleteTask = {
                    viewModel.deleteTask(LocalDate.now(), it)
                }
            )
        }
    }
}
