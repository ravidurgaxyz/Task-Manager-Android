package com.ravixyz.taskmanager.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.ravixyz.taskmanager.model.Task
import com.ravixyz.taskmanager.model.TaskManagerViewModel
import com.ravixyz.taskmanager.model.TaskStatus
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TimePickerTextField(
    time: LocalTime,
    onTrailingIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    OutlinedTextField(
        value = "%02d : %02d".format(
            time.hour,
            time.minute
        ),
        modifier = modifier,
        onValueChange = { },
        readOnly = true,
        trailingIcon = {
            IconButton(
                onClick = onTrailingIconClick,
                enabled = enabled
            ) {
                Image(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Select Time",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }
    )
}

@Composable
fun DatePickerTextField(
    date: LocalDate,
    onTrailingIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    OutlinedTextField(
        value = "%02d/%02d/%02d".format(
            date.dayOfMonth,
            date.monthValue,
            date.year
        ),
        modifier = modifier,
        onValueChange = { },
        readOnly = true,
        trailingIcon = {
            IconButton(
                onClick = onTrailingIconClick,
                enabled = enabled
            ) {
                Image(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select Date",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }
    )
}

@Composable
fun CalendarTaskItem(
    task: Task,
    onClick: () -> Unit,
    deleteTask: () -> Unit,
    deleteAllTasks: () -> Unit
){
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDeleteDialog){
        AlertDialog(
            title = {
                Text("Task Manager")
            },
            text = {
                Text("Do you really want to delete ${task.title}?")
            },
            icon = {
            },
            onDismissRequest = { showDeleteDialog = false },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("No")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteTask()
                    Toast.makeText(
                        context,
                        "Deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    showDeleteDialog = false
                }) {
                    Text("Yes")
                }
            }
        )
    }
    if (showDeleteAllDialog){
        DeleteAllDialog(
            task = task,
            onDismiss = { showDeleteAllDialog = false },
            onDeleteTask = { deleteTask() },
            onDeleteAllTasks = { deleteAllTasks() }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = if (task.status != TaskStatus.DONE)Icons.AutoMirrored.Default.Assignment else Icons.Default.AssignmentTurnedIn,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "%02d:%02d - %02d:%02d".format(
                        task.fromTime.hour,
                        task.fromTime.minute,
                        task.toTime.hour,
                        task.toTime.minute
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box {
                IconButton(
                    onClick = {
                        if (task.id > 118u) showDeleteAllDialog = true
                        else showDeleteDialog = true
                    },
                    enabled = task.status != TaskStatus.DONE
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MiniTaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    deleteTask: () -> Unit
) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDeleteDialog){
        AlertDialog(
            title = {
                Text("Task Manager")
            },
            text = {
                Text("Do you really want to delete ${task.title}?")
            },
            icon = {},
            onDismissRequest = { showDeleteDialog = false },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("No")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteTask()
                    Toast.makeText(
                        context,
                        "Deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    showDeleteDialog = false
                }) {
                    Text("Yes")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .alpha(if (task.status != TaskStatus.DONE) 1f else 0.6f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            // Left Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (task.status != TaskStatus.DONE)Icons.AutoMirrored.Default.Assignment else Icons.Default.AssignmentTurnedIn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "%02d:%02d - %02d:%02d".format(
                        task.fromTime.hour,
                        task.fromTime.minute,
                        task.toTime.hour,
                        task.toTime.minute
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = modifier.align(Alignment.CenterVertically)
            ) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    enabled = task.status != TaskStatus.DONE
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "More options"
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onAttachmentClick: (String) -> Unit,
    onAddAttachmentClick: () -> Unit,
    onDone: (String) -> Unit
) {

    var report by remember { mutableStateOf(task.report) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(modifier = Modifier
            .padding(24.dp)
        ) {

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "%02d:%02d - %02d:%02d".format(
                    task.fromTime.hour,
                    task.fromTime.minute,
                    task.toTime.hour,
                    task.toTime.minute
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            val statusColor =
                if (task.status == TaskStatus.DONE) Color.Green else Color.Red

            Text(
                text = task.status.name.uppercase(),
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = report,
                onValueChange = { report = it },
                label = { Text("Report") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                readOnly = task.status == TaskStatus.DONE
            )

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                items(task.attachments) { attachment ->
                    IconButton(onClick = {
                        onAttachmentClick(attachment)
                    }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                            contentDescription = attachment
                        )
                    }
                }

                item {
                    IconButton(onClick = {
                        onAddAttachmentClick()
                        Toast.makeText(
                            context,
                            "Attachment Added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add attachment"
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Done Button
            Button(
                onClick = {
                    onDone(report.trim())
                    Toast.makeText(
                        context,
                        "Done",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.align(Alignment.End),
                enabled = task.status != TaskStatus.DONE
            ) {
                Text("Done")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    viewModel: TaskManagerViewModel,
    canShow: () -> Boolean,
    onDismiss: () -> Unit
){

    val selectedDate = viewModel.selectedDate.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fromTime by remember { mutableStateOf(LocalTime.now()) }
    var showFromTimePicker by remember { mutableStateOf(false) }
    var toTime by remember { mutableStateOf(LocalTime.now().plusHours(1L)) }
    var showToTimePicker by remember { mutableStateOf(false) }

    var repeat by remember { mutableStateOf(false) }
    val daysOfWeek = remember { mutableStateListOf<DayOfWeek>() }

    var fromDate by remember { mutableStateOf(selectedDate.value) }
    var toDate by remember { mutableStateOf(selectedDate.value.plusDays(1L)) }
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    var isSaved by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // BOTTOM SHEET FOR ADD TASK OPERATION
    if (canShow()) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                title = ""
                description = ""
                fromTime = LocalTime.now()
                toTime = LocalTime.now().plusHours(1L)
                repeat = false
                onDismiss()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        .heightIn(max = 256.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp)
                ) {
                    TimePickerTextField(
                        time = fromTime,
                        onTrailingIconClick = { showFromTimePicker = true },
                        modifier = Modifier
                            .weight(1f)
                    )
                    Text(
                        text = "-",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TimePickerTextField(
                        time = toTime,
                        onTrailingIconClick = { showToTimePicker = true},
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                ) {
                    Text(
                        text = "Repeat",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                    Switch(
                        checked = repeat,
                        onCheckedChange = { repeat = it },
                        modifier = Modifier
                    )
                }
                if (repeat) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 8.dp)
                    ) {
                        DatePickerTextField(
                            date = fromDate,
                            onTrailingIconClick = { showFromDatePicker = true },
                            modifier = Modifier
                                .weight(1f)
                        )
                        Text(
                            text = "-",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        DatePickerTextField(
                            date = toDate,
                            onTrailingIconClick = { showToDatePicker = true },
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                    ) {
                        DayOfWeek.entries.forEach { day ->
                            DayButton(
                                Modifier,
                                day = day
                            ) { isSelected ->
                                if (isSelected) daysOfWeek.add(day) else daysOfWeek.removeIf { it == day }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        if (
                            !repeat &&
                            title.trim() != "" &&
                            fromTime < toTime
                        ) {
                            viewModel.addTask(
                                Task(
                                    title = title.trim(),
                                    description = description.trim(),
                                    fromTime = fromTime,
                                    toTime = toTime
                                )
                            )
                            isSaved = true
                        } else if (
                            repeat &&
                            title.trim() != "" &&
                            fromTime < toTime &&
                            fromDate < toDate &&
                            daysOfWeek.isNotEmpty()
                        ) {
                            viewModel.addRepeatedTasks(
                                fromDate,
                                toDate,
                                daysOfWeek,
                                Task(
                                    title = title.trim(),
                                    description = description.trim(),
                                    fromTime = fromTime,
                                    toTime = toTime,
                                )
                            )
                            isSaved = true
                        }
                        if (isSaved) {
                            Toast.makeText(
                                context,
                                "Saved",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.refresh()
                            title = ""
                            description = ""
                            fromTime = LocalTime.now()
                            toTime = LocalTime.now().plusHours(1L)
                            repeat = false
                            daysOfWeek.clear()
                            onDismiss()
                        }
                        else
                            Toast.makeText(
                                context,
                                "Not Saved",
                                Toast.LENGTH_SHORT
                            ).show()
                        isSaved = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                ) {
                    Text(
                        text = "Save",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(4.dp)
                    )
                }
            }
        }

        if (showFromTimePicker){
            TimePickerDialog(
                onDismiss = { showFromTimePicker = false },
                onConfirm = {
                    fromTime = it
                    showFromTimePicker = false
                },
                initialTime = fromTime.minusMinutes(fromTime.minute.toLong())
            )
        }

        if (showToTimePicker){
            TimePickerDialog(
                onDismiss = { showToTimePicker = false },
                onConfirm = {
                    toTime = it
                    showToTimePicker = false
                },
                initialTime = toTime.minusMinutes(toTime.minute.toLong())
            )
        }

        if (showFromDatePicker){
            CustomDatePickerDialog(
                initialDate = selectedDate.value.plusDays(1L),
                onDismiss = { showFromDatePicker = false },
                onConfirm = {
                    fromDate = it
                    showFromDatePicker = false
                }
            )
        }

        if (showToDatePicker){
            CustomDatePickerDialog(
                initialDate = selectedDate.value.plusDays(2L),
                onDismiss = { showToDatePicker = false },
                onConfirm = {
                    toDate = it
                    showToDatePicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timeState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(LocalTime.of(timeState.hour, timeState.minute))
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timeState)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    backAction: () -> Unit,
    navigate: () -> Unit
){
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {
            if (title == "Settings"){
                IconButton(
                    onClick = backAction
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        actions = {
            if (title == "Task Manager") {
                IconButton(
                    onClick = navigate
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskStatusDropdown(
    modifier: Modifier = Modifier,
    status: TaskStatus = TaskStatus.PENDING,
    enabled: Boolean,
    onStatusChange: (TaskStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(status) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                enabled = enabled
            ),
            value = selectedStatus.name,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Task Status") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TaskStatus.entries.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        selectedStatus = item
                        expanded = false
                        onStatusChange(item)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskSheet(
    viewModel: TaskManagerViewModel,
    canShow: () -> Boolean,
    onDismiss: () -> Unit
){

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val task = viewModel.currentEditingTask.collectAsState()
    val selectedDate = viewModel.selectedDate.collectAsState()

    var date by remember { mutableStateOf(selectedDate.value) }
    var showDatePicker by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(task.value.title) }
    var description by remember { mutableStateOf(task.value.description) }
    var fromTime by remember { mutableStateOf(task.value.fromTime) }
    var showFromTimePicker by remember { mutableStateOf(false) }
    var toTime by remember { mutableStateOf(task.value.toTime) }
    var showToTimePicker by remember { mutableStateOf(false) }
    var taskStatus by remember { mutableStateOf(task.value.status) }
    var report by remember { mutableStateOf(task.value.report) }
    val attachments = remember { mutableStateListOf<String>().apply {  addAll(task.value.attachments) } }
    val addedAttachments = remember { mutableStateListOf<String>() }
    val copiedAttachmentsUri = remember { mutableStateListOf<String>() }
    val deletedAttachments = remember { mutableStateListOf<String>() }

    var editable by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(true) }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                addedAttachments.add(it.toString())
            }
        }

    // BOTTOM SHEET FOR EDIT TASK OPERATION
    if (canShow()) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                title = ""
                description = ""
                fromTime = LocalTime.now()
                toTime = LocalTime.now().plusHours(1L)
                onDismiss()
            }
        ) {
            if (!editable) {
                Text(
                    text = "Edit",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(end = 28.dp)
                        .align(Alignment.End)
                        .clickable(
                            onClick = { editable = true }
                        )
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        readOnly = !editable,
                        label = { Text("Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        minLines = 3,
                        readOnly = !editable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                            .heightIn(max = 256.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = "%02d/%02d/%04d".format(
                            date.dayOfMonth,
                            date.monthValue,
                            date.year
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { showDatePicker = true },
                                enabled = editable
                            ) {
                                Image(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Select Time",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    )
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp)
                    ) {
                        TimePickerTextField(
                            time = fromTime,
                            enabled = editable,
                            onTrailingIconClick = { showFromTimePicker = true},
                            modifier = Modifier
                                .weight(1f)
                        )
                        Text(
                            text = "-",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TimePickerTextField(
                            time = toTime,
                            enabled = editable,
                            onTrailingIconClick = { showToTimePicker = true},
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
                item {
                    TaskStatusDropdown(
                        status = taskStatus,
                        onStatusChange = { taskStatus = it },
                        enabled = editable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 8.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = report,
                        onValueChange = { report = it },
                        label = { Text("Report") },
                        minLines = 3,
                        readOnly = !editable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 8.dp)
                            .heightIn(max = 256.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = "Attachments ( ${attachments.size + addedAttachments.size} )",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 16.dp),
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    viewModel.setCurrentAttachingTask(task.value)
                                    launcher.launch(arrayOf("*/*"))
                                },
                                enabled = editable
                            ) {
                                Image(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Attachment",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    )
                }
                items(attachments){ file ->
                    OutlinedTextField(
                        value = viewModel.getAttachmentFileName(context, file.toUri())!!,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                        ,
                        trailingIcon = {
                            Row {
                                IconButton(
                                    onClick = {
                                        viewModel.openFile(context, file)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = "Open file"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        deletedAttachments.add(file)
                                        attachments.remove(file)
                                    },
                                    enabled = editable
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove file"
                                    )
                                }
                            }
                        }
                    )
                }
                items(addedAttachments){ file ->
                    OutlinedTextField(
                        value = viewModel.getAttachmentFileName(context, file.toUri(), absolute = true)!!,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                        ,
                        trailingIcon = {
                            Row {
                                IconButton(
                                    onClick = {
                                        viewModel.openFile(context, file)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = "Open file"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        addedAttachments.remove(file)
                                    },
                                    enabled = editable
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove file"
                                    )
                                }
                            }
                        }
                    )
                }
                item {
                    Button(
                        enabled = editable,
                        onClick = {
                            if (
                                title.trim() != "" &&
                                fromTime < toTime &&
                                if (taskStatus != TaskStatus.DONE) date >= LocalDate.now() else true
                            ) {
                                addedAttachments.forEach {
                                    viewModel.copyFileToDocuments(context, it.toUri())
                                    copiedAttachmentsUri.add(viewModel.currentAttachedFileName.value)
                                }
                                deletedAttachments.forEach {
                                    viewModel.deleteFile(context, it.toUri())
                                }
                                val attachmentsTemp: MutableList<String> =
                                    emptyList<String>().toMutableList()
                                attachmentsTemp.apply {
                                    addAll(attachments)
                                    addAll(copiedAttachmentsUri)
                                }
                                val aTask = Task(
                                    id = task.value.id,
                                    title = title.trim(),
                                    description = description.trim(),
                                    fromTime = fromTime,
                                    toTime = toTime,
                                    status = taskStatus,
                                    report = report.trim(),
                                    attachments = attachmentsTemp.toList()
                                )
                                if (date != selectedDate.value) {
                                    viewModel.deleteTask(selectedDate.value, task.value)
                                    viewModel.addTask(aTask, date)
                                } else viewModel.updateTask(date, aTask)
                                isSaved = true
                                onDismiss()
                            }
                            if (isSaved){
                                Toast.makeText(
                                    context,
                                    "Saved",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else {
                                Toast.makeText(
                                    context,
                                    "Not Saved",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            isSaved = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        if (showFromTimePicker){
            TimePickerDialog(
                onDismiss = { showFromTimePicker = false },
                onConfirm = {
                    fromTime = it
                    showFromTimePicker = false
                },
                initialTime = fromTime.minusMinutes(fromTime.minute.toLong())
            )
        }

        if (showToTimePicker){
            TimePickerDialog(
                onDismiss = { showToTimePicker = false },
                onConfirm = {
                    toTime = it
                    showToTimePicker = false
                },
                initialTime = toTime.minusMinutes(toTime.minute.toLong())
            )
        }

        if (showDatePicker){
            CustomDatePickerDialog(
                initialDate = selectedDate.value.plusDays(1L),
                onDismiss = { showDatePicker = false },
                onConfirm = {
                    date = it
                    showDatePicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onConfirm(selectedDate)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DayButton(
    modifier: Modifier = Modifier,
    day: DayOfWeek,
    onSelectionChanged: (Boolean) -> Unit
){
    var value by remember { mutableStateOf(false) }

    Button(
        onClick = {
            value = !value
            onSelectionChanged(value)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        ),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .size(48.dp)
            .shadow(elevation = 4.dp, shape = CircleShape)
    ) {
        Text(
            color = if (value) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onBackground,
            text = day
                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                .lowercase()
                .replaceFirstChar { it.uppercase() }
        )
    }

}

@Composable
fun DeleteAllDialog(
    task: Task,
    onDismiss: () -> Unit,
    onDeleteTask: () -> Unit,
    onDeleteAllTasks: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(324.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Do you want to delete the task ${task.title}?",
                    modifier = Modifier.padding(16.dp),
                )
                Button(
                    onClick = { onDismiss() },
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .shadow(2.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onDeleteTask()
                        Toast.makeText(
                            context,
                            "Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismiss() },
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .shadow(2.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Delete this task only")
                }
                Button(
                    onClick = {
                        onDeleteAllTasks()
                        Toast.makeText(
                            context,
                            "Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismiss() },
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .shadow(2.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Delete with all future tasks")
                }
            }
        }
    }
}
