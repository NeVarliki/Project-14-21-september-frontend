package ru.myitschool.work.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object WorkTheme {
    val colors: WorkColors
        @Composable
        @ReadOnlyComposable
        get() = LocalWorkColors.current
}

@Composable
fun WorkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkWorkColors else LightWorkColors
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.accent,
            onPrimary = colors.onAccent,
            background = colors.background,
            surface = colors.background,
            onBackground = colors.text,
            onSurface = colors.text,
            error = colors.error,
        )
    } else {
        lightColorScheme(
            primary = colors.accent,
            onPrimary = colors.onAccent,
            background = colors.background,
            surface = colors.background,
            onBackground = colors.text,
            onSurface = colors.text,
            error = colors.error,
        )
    }
    CompositionLocalProvider(LocalWorkColors provides colors) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            content = content
        )
    }
}
