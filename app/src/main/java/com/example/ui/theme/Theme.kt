package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MedsBlueDark,
    onPrimary = MedsBlueOnContainerLight,
    primaryContainer = MedsBlueDarkContainer,
    onPrimaryContainer = MedsBlueOnContainerDark,
    secondary = AlarmRedDark,
    onSecondary = AlarmRedOnContainerLight,
    secondaryContainer = AlarmRedDarkContainer,
    onSecondaryContainer = AlarmRedOnContainerDark,
    tertiary = SubtitleYellowDark,
    onTertiary = SubtitleYellowContainerDark,
    tertiaryContainer = SubtitleYellowContainerDark,
    onTertiaryContainer = SubtitleYellowDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = MedsBlue,
    onPrimary = LightSurface,
    primaryContainer = MedsBlueLightContainer,
    onPrimaryContainer = MedsBlueOnContainerLight,
    secondary = AlarmRed,
    onSecondary = LightSurface,
    secondaryContainer = AlarmRedLightContainer,
    onSecondaryContainer = AlarmRedOnContainerLight,
    tertiary = SubtitleYellow,
    onTertiary = LightSurface,
    tertiaryContainer = SubtitleYellowContainerLight,
    onTertiaryContainer = SubtitleYellowContainerDark,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We prioritize the app's signature MedsAlarm branding, while allowing dynamicColor option
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val windowInsetsController = WindowCompat.getInsetsController(window, view)
                // In light mode, status bar icons should be dark; in dark mode, light
                windowInsetsController.isAppearanceLightStatusBars = !darkTheme
                windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
