package com.ravixyz.taskmanager.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.ravixyz.taskmanager.model.TaskManagerViewModel
import com.ravixyz.taskmanager.ui.CalendarTaskItem
import com.ravixyz.taskmanager.ui.EditTaskSheet
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTab(
    viewModel: TaskManagerViewModel
){

    val selectedDate = viewModel.selectedDate.collectAsState()
    val calendarTasks = viewModel.calendarTasks.collectAsState()

    val datePickerState = rememberDatePickerState()

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            val date = Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            if (date != null && date != selectedDate){
                viewModel.selectDate(date)
            }
        }
    }

    var onTaskEditing by remember { mutableStateOf(false) }

    if (onTaskEditing){
        EditTaskSheet(
            viewModel = viewModel,
            canShow = { onTaskEditing },
            onDismiss = { onTaskEditing = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(elevation = 8.dp)
                , colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        items(calendarTasks.value){ task->
            CalendarTaskItem(
                task = task,
                onClick = {
                    viewModel.setCurrentEditingTask(task)
                    onTaskEditing = true
                },
                deleteTask = {
                    viewModel.deleteTask(selectedDate.value, task)
                },
                deleteAllTasks = {
                    viewModel.deleteWithFutureTasks(
                        date = selectedDate.value,
                        task = task
                    )
                }
            )
        }

        item{
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

