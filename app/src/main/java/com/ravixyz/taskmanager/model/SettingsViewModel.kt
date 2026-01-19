package com.ravixyz.taskmanager.model

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class SettingsViewModel: ViewModel(){

    private val _config = MutableStateFlow(Configuration())
    val config: StateFlow<Configuration> = _config

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun readConfig(context: Context){
        val configFile = File(context.getExternalFilesDir(null), "app.cfg").apply {
            if (!exists()){
                val string = json.encodeToString(_config.value)
                writeText(string)
            }
        }
        _config.value = json.decodeFromString<Configuration>(configFile.readText())
    }

    private fun writeConfig(context: Context, config: Configuration){
        val configFile = File(context.getExternalFilesDir(null), "app.cfg").apply {
            if (!exists()){
                val string = json.encodeToString(120u)
                writeText(string)
            }
        }
        configFile.writeText(json.encodeToString(config))
    }

    fun setConfig(context: Context, config: Configuration){
        _config.value = config
        writeConfig(context, config)
    }

    fun exportData(context: Context, uri: Uri){
        val rootDir = context.getExternalFilesDir(null)!!
        val sourceDir = File(context.getExternalFilesDir(null), "tasks")
        val zipFile = File(context.cacheDir, "Task Data.zip")

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            sourceDir.walkTopDown()
                .filter { it.isFile }
                .forEach { file ->
                    val entryName = "tasks/${file.relativeTo(sourceDir).path}"
                    zos.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { it.copyTo(zos) }
                    zos.closeEntry()
                }
            val taskConfigFile = File(rootDir, "task.cfg")
            if(taskConfigFile.exists()){
                zos.putNextEntry(ZipEntry("task.cfg"))
                taskConfigFile.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
        val targetDir = DocumentFile.fromTreeUri(context, uri)!!
        val zipDoc = targetDir.createFile("application/zip", "Task Data.zip")!!

        context.contentResolver.openOutputStream(zipDoc.uri)!!.use { out ->
            zipFile.inputStream().use { it.copyTo(out) }
        }
    }

    fun importFromZip(context: Context, uri: Uri){
        clearAllData(context)
        importData(context, uri)
    }

    private fun importData(context: Context, zipUri: Uri) {

        val resolver = context.contentResolver
        val rootDir = context.getExternalFilesDir(null)!!
        val tasksDir = File(rootDir, "tasks")

        if (!tasksDir.exists()) tasksDir.mkdirs()

        resolver.openInputStream(zipUri)!!.use { input ->
            ZipInputStream(BufferedInputStream(input)).use { zis ->
                var entry: ZipEntry?

                while (zis.nextEntry.also { entry = it } != null) {
                    val name = entry!!.name

                    when {
                        name == "task.cfg" -> {
                            val outFile = File(rootDir, "task.cfg")
                            outFile.outputStream().use { zis.copyTo(it) }
                        }

                        name.startsWith("tasks/") -> {
                            val relative = name.removePrefix("tasks/")
                            val outFile = File(tasksDir, relative)

                            if (entry.isDirectory) {
                                outFile.mkdirs()
                            } else {
                                outFile.parentFile?.mkdirs()
                                outFile.outputStream().use { zis.copyTo(it) }
                            }
                        }
                    }
                    zis.closeEntry()
                }
            }
        }
    }

    fun clearAllData(context: Context){
        File(context.getExternalFilesDir(null), "tasks").deleteRecursively()
        File(context.getExternalFilesDir(null), "task.cfg").apply {
            if (exists()){
                delete()
            }
        }
    }
}