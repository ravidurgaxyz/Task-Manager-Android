package com.ravixyz.taskmanager.file

import android.content.Context
import com.ravixyz.taskmanager.model.Task
import kotlinx.serialization.json.Json
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate

class TaskFileManager(
    private val context: Context
) {

    private val taskDir: File by lazy {
        File(context.getExternalFilesDir(null), "tasks").apply {
            if (!exists()) mkdirs()
        }
    }

    private val configFile: File by lazy {
        File(context.getExternalFilesDir(null), "task.cfg").apply {
            if (!exists()){
                val string = json.encodeToString(120u)
                writeText(string)
            }
        }
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val _currentIDs: MutableList<UInt> = mutableListOf()

    fun writeTasks(
        date: LocalDate,
        allTasks: List<Task>
    ){
        if (allTasks.isEmpty()) {
            val aFile = getTaskFile(date)
            if (aFile.exists()) aFile.delete()
            return
        }
        val tasks = allTasks.sortedBy { it.fromTime }
        val file = getTaskFile(date)

        val content = json.encodeToString(tasks)
        file.writeText(content)
    }

    fun readTasks(date: LocalDate): List<Task>{
        val file = getTaskFile(date)
        if (!file.exists()) return emptyList()

        val string = file.readText()
        val tasks: List<Task> =  json.decodeFromString(string)
        _currentIDs.clear()
        tasks.forEach {
            _currentIDs.add(it.id)
        }
        return tasks
    }

    fun addTask(
        date: LocalDate,
        task: Task,
        repeated: Boolean = false
    ){

        val tasks = readTasks(date)

        var id = 0u
        if (!repeated) {
            for (i in 1..118) {
                if (i.toUInt() !in _currentIDs) {
                    id = i.toUInt()
                    break
                }
            }
        } else id = task.id

        val allTasks = tasks.toMutableList()
        allTasks.add(task.copy(id = id))
        writeTasks(date, allTasks.toList())
    }

    fun updateTask(date: LocalDate, task: Task){
        val allTasks = readTasks(date).toMutableList()
        allTasks.removeIf { it.id == task.id }
        allTasks.add(task)
        writeTasks(date, allTasks.toList())
    }

    fun deleteTask(date: LocalDate, task: Task){
        val tasks = readTasks(date).toMutableList()
        tasks.apply {
            removeIf { it.id == task.id }
        }
        writeTasks(date, tasks)
    }

    fun addRepeatedTasks(
        fromDate: LocalDate,
        toDate: LocalDate,
        days: List<DayOfWeek>,
        task: Task
    ){
        var date = fromDate
        val id = getCurrentAvailableId()
        setCurrentAvailable(id + 1u)
        while (date <= toDate){
            if (date.dayOfWeek in days){
                addTask(date, task.copy(id = id), true)
            }
            date = date.plusDays(1L)
        }
    }

    fun deleteWithFutureTasks(startDate: LocalDate, task: Task){
        if (task.id <= 118u) return
        val files = taskDir.listFiles()
        var fileName: String
        var year: Int
        var month: Int
        var date: Int
        var deleteFlag = false
        if (files != null) {
            for (file in files) {
                fileName = file.name
                year = fileName.substring(6, 10).toInt()
                month = fileName.substring(3, 5).toInt()
                date = fileName.substring(0, 2).toInt()
                if (year >= startDate.year){
                    if (year > startDate.year) deleteFlag = true
                    else if (month >= startDate.monthValue && !deleteFlag){
                        if (month > startDate.monthValue) deleteFlag = true
                        else if (date >= startDate.dayOfMonth) deleteFlag = true
                    }
                }
                if (deleteFlag){
                    deleteTask(LocalDate.of(year, month, date), task)
                }
            }
        }
    }

    private fun getTaskFile(date: LocalDate): File{
        val fileName = "%02d_%02d_%04d.task".format(
            date.dayOfMonth,
            date.monthValue,
            date.year
        )
        return File(taskDir, fileName)
    }

    fun getCurrentAvailableId(): UInt{
        return json.decodeFromString<UInt>(configFile.readText())
    }

    fun setCurrentAvailable(value: UInt){
        configFile.writeText(json.encodeToString(value))
    }
}
