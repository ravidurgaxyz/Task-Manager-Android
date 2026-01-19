package com.ravixyz.taskmanager.model

import kotlinx.serialization.Serializable

@Serializable
enum class ColorTheme{
    SYSTEM,
    LIGHT,
    DARK
}

@Serializable
data class Configuration(
    val colorTheme: ColorTheme = ColorTheme.LIGHT
)
