package com.ravixyz.taskmanager.model

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ravixyz.taskmanager.file.TaskFileManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TaskManagerViewModel(app: Application): AndroidViewModel(app) {

    private val fileManager = TaskFileManager(app)

    // Variables for Current Task Tab

    private val _currentTasks = MutableStateFlow<List<Task>>(emptyList())
    val currentTasks: StateFlow<List<Task>> = _currentTasks

    // Variables for All Tasks Tab

    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks

    // Variables for Calendar Tab
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _calendarTasks = MutableStateFlow<List<Task>>(emptyList())
    val calendarTasks: StateFlow<List<Task>> = _calendarTasks

    // Attachment Variables for Task Tab
    private val _currentAttachedFileName = MutableStateFlow("")
    val currentAttachedFileName: StateFlow<String> = _currentAttachedFileName
    private val _currentAttachingTask = MutableStateFlow(getDefaultTask())
    val currentAttachingTask: StateFlow<Task> = _currentAttachingTask

    // Editing Task Variables for Calendar Tab
    private val _currentEditingTask =  MutableStateFlow(getDefaultTask())
    val currentEditingTask: StateFlow<Task> = _currentEditingTask

    init {
        refresh()
        viewModelScope.launch {
            while (true) {
                val now = LocalTime.now()
                val delayMillis =
                    (60 - now.second) * 1000L - now.nano / 1_000_000

                delay(delayMillis.coerceAtLeast(0))

                refresh()
            }
        }
    }

    fun refresh(){
        selectDate(_selectedDate.value)
        _todayTasks.value = fileManager.readTasks(LocalDate.now()).let { list ->
            list.sortedBy { it.status }
        }
        setCurrentTasks()
    }

    fun setCurrentTasks(){
        val time = LocalTime.now()
        val current = _todayTasks.value.filter { it.fromTime <= time && time <= it.toTime && it.status != TaskStatus.DONE }
        val previous = _todayTasks.value.filter { it.fromTime < time && it.status != TaskStatus.DONE }
        val next = _todayTasks.value.filter { it.toTime > time && it.status != TaskStatus.DONE }
        if (current.isEmpty()){
            if (previous.isEmpty()){
                if (next.isEmpty()){
                    _currentTasks.value = _todayTasks.value
                    return
                }
                _currentTasks.value = next.toList()
                return
            }
            _currentTasks.value = previous.toList()
            return
        }
        _currentTasks.value = current.toList()
    }

    fun selectDate(date: LocalDate){
        _selectedDate.value = date
        _calendarTasks.value = fileManager.readTasks(date).let {
                list ->
            list.sortedBy { it.status }
        }
    }

    fun addTask(task: Task, date: LocalDate = _selectedDate.value){
        fileManager.addTask(date, task)
        refresh()
    }

    fun addRepeatedTasks(
        fromDate: LocalDate,
        toDate: LocalDate,
        daysOfWeek: List<DayOfWeek>,
        task: Task
    ){
        fileManager.addRepeatedTasks(
            fromDate,
            toDate,
            daysOfWeek,
            task
        )
        refresh()
    }

    fun setCurrentAttachingTask(task: Task){
        _currentAttachingTask.value = task
    }

    fun addAttachment(date: LocalDate, task: Task){
        fileManager.updateTask(date, task)
        _currentAttachedFileName.value = ""
        _currentAttachingTask.value = getDefaultTask()
        refresh()
    }

    fun markAsDone(date: LocalDate, task: Task){
        fileManager.updateTask(date, task.copy(
            status = TaskStatus.DONE
        ))
        refresh()
    }

    fun setCurrentEditingTask(task: Task){
        _currentEditingTask.value = task
    }

    fun deleteTask(date: LocalDate, task: Task){
        fileManager.deleteTask(date, task)
        refresh()
    }

    fun updateTask(date: LocalDate, task: Task){
        fileManager.updateTask(date, task)
        refresh()
    }

    fun deleteWithFutureTasks(date: LocalDate, task: Task){
        fileManager.deleteWithFutureTasks(date, task)
        refresh()
    }














    // Imported Functions for File Operations
    fun copyFileToDocuments(context: Context, sourceUri: Uri) {

        val resolver = context.contentResolver
        val fileName =
            LocalDate.now().toString() + "_" +
                    LocalTime.now().toString() + "_" +
                    getFileName(resolver, sourceUri)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + "/Task Manager/attachments"
            )
        }

        val targetUri = resolver.insert(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
            values
        ) ?: return

        resolver.openInputStream(sourceUri)?.use { input ->
            resolver.openOutputStream(targetUri)?.use { output ->
                input.copyTo(output)
            }
        }

        // ✅ STORE URI, NOT NAME
        _currentAttachedFileName.value = targetUri.toString()
    }
    fun getFileName(resolver: ContentResolver, uri: Uri): String {
        var name = "file"

        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) {
                name = cursor.getString(index)
            }
        }
        return name
    }
    fun openFile(context: Context, uriString: String) {

        val uri = uriString.toUri()
        val mimeType =
            context.contentResolver.getType(uri) ?: "*/*"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(
            Intent.createChooser(intent, "Open with")
        )
    }
    fun getAttachmentFileName(context: Context, uri: Uri, absolute: Boolean = false): String? {
        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )
        if (absolute){
            cursor?.use {
                val nameIndex =  it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst() && nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex != -1) {
                return extractFileName(it.getString(nameIndex))
            }
        }
        return null
    }
    fun extractFileName(fileName: String): String {
        val regex =
            """\d{4}-\d{2}-\d{2}_\d{2}_\d{2}_\d{2}\.\d+_(.+\.[^.]+)""".toRegex()

        return regex.find(fileName)?.groupValues?.get(1) ?: fileName
    }
    fun deleteFile(context: Context, uri: Uri): Boolean {
        return try {
            val rows = context.contentResolver.delete(uri, null, null)
            rows > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}