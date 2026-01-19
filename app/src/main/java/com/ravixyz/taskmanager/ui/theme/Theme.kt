package com.ravixyz.taskmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.ravixyz.taskmanager.model.ColorTheme
import com.ravixyz.taskmanager.model.SettingsViewModel

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = onPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    background = Background,
    onBackground = onBackground
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    background = BackgroundDark,
    onBackground = onBackgroundDark
)

@Composable
fun TaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    viewModel: SettingsViewModel,
    content: @Composable () -> Unit
) {
    viewModel.readConfig(LocalContext.current)
    val config = viewModel.config.collectAsState()

    val colorScheme = when (config.value.colorTheme){
        ColorTheme.LIGHT -> LightColorScheme
        ColorTheme.DARK -> DarkColorScheme
        ColorTheme.SYSTEM -> when {
            dynamicColor -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
