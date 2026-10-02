package com.example.scontatore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RedPrimaryLight,
    onPrimary = RedOnPrimaryLight,
    primaryContainer = RedPrimaryContainerLight,
    onPrimaryContainer = RedOnPrimaryContainerLight,
    secondary = RedSecondaryLight,
    secondaryContainer = RedSecondaryContainerLight,
    onSecondaryContainer = RedOnSecondaryContainerLight,
    tertiary = RedTertiaryLight,
    background = RedSurfaceLight,
    onBackground = RedOnSurfaceLight,
    surface = RedSurfaceLight,
    onSurface = RedOnSurfaceLight,
    surfaceVariant = RedSurfaceVariantLight,
    onSurfaceVariant = RedOnSurfaceVariantLight,
    outline = RedOutlineLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimaryDark,
    onPrimary = RedOnPrimaryDark,
    primaryContainer = RedPrimaryContainerDark,
    onPrimaryContainer = RedOnPrimaryContainerDark,
    secondary = RedSecondaryDark,
    secondaryContainer = RedSecondaryContainerDark,
    onSecondaryContainer = RedOnSecondaryContainerDark,
    tertiary = RedTertiaryDark,
    background = RedSurfaceDark,
    onBackground = RedOnSurfaceDark,
    surface = RedSurfaceDark,
    onSurface = RedOnSurfaceDark,
    surfaceVariant = RedSurfaceVariantDark,
    onSurfaceVariant = RedOnSurfaceVariantDark,
    outline = RedOutlineDark,
)

@Composable
fun ScontatoreTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
