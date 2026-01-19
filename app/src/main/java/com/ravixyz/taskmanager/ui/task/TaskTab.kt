package com.ravixyz.taskmanager.ui.task

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ravixyz.taskmanager.model.TaskManagerViewModel
import com.ravixyz.taskmanager.ui.TaskCard
import java.time.LocalDate

@Composable
fun TaskTab(
    viewModel: TaskManagerViewModel
){
    val currentTasks = viewModel.currentTasks.collectAsState()
    val attachedFileName = viewModel.currentAttachedFileName.collectAsState()
    val currentAttachingTask = viewModel.currentAttachingTask.collectAsState()
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                viewModel.copyFileToDocuments(context, it)
            }
        }
    LaunchedEffect(attachedFileName.value) {
        if (attachedFileName.value != "") {
            val attachments = currentAttachingTask.value.attachments.toMutableList()
            attachments.add(attachedFileName.value)
            viewModel.addAttachment(
                LocalDate.now(),
                currentAttachingTask.value.copy(
                    attachments = attachments.toList()
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(currentTasks.value){ task ->
            TaskCard(
                task = task,
                onAttachmentClick = {
                    viewModel.openFile(context, it)
                },
                onAddAttachmentClick = {
                    viewModel.setCurrentAttachingTask(task)
                    launcher.launch(arrayOf("*/*"))
                },
                onDone = {
                    viewModel.markAsDone(
                        LocalDate.now(),
                        task.copy(
                            report = it
                        )
                    )
                }
            )
        }
    }
}
