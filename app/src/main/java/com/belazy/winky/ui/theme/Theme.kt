package com.belazy.winky.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = WinkyPrimary,
    secondary = WinkySecondary,
    background = WinkyBackground,
    surface = WinkySurface,
    error = WinkyError,
    onPrimary = WinkyOnPrimary,
    onSecondary = WinkyOnSecondary,
    onBackground = WinkyOnBackground,
    onSurface = WinkyOnSurface,
    onError = WinkyOnError
)

private val DarkColorScheme = darkColorScheme(
    primary = WinkyPrimary,
    secondary = WinkySecondary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = WinkyError,
    onPrimary = WinkyOnPrimary,
    onSecondary = WinkyOnSecondary,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = WinkyOnError
)

@Composable
fun GalleryAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                window.statusBarColor = colorScheme.background.toArgb()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
