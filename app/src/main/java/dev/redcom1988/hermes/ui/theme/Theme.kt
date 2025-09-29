package dev.redcom1988.hermes.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.ui.screen.settings.SettingsPreference
import dev.redcom1988.hermes.ui.util.collectAsState

@Composable
fun HermesTheme(
    content: @Composable () -> Unit
) {
    val settingsPreference by remember { injectLazy<SettingsPreference>() }
    val appTheme by settingsPreference.appTheme().collectAsState()

    val colorScheme = when (appTheme) {
        SettingsPreference.AppTheme.LIGHT -> FilamentLightColorScheme
        SettingsPreference.AppTheme.DARK -> FilamentDarkColorScheme
        SettingsPreference.AppTheme.SYSTEM ->
            if (isSystemInDarkTheme()) FilamentDarkColorScheme else FilamentLightColorScheme
    }

    SystemBarColor(
        color = colorScheme.surface,
        darkTheme = when (appTheme) {
            SettingsPreference.AppTheme.LIGHT -> false
            SettingsPreference.AppTheme.DARK -> true
            SettingsPreference.AppTheme.SYSTEM -> isSystemInDarkTheme()
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

@Suppress("DEPRECATION")
@Composable
fun SystemBarColor(color: Color?, darkTheme: Boolean = false) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            color?.let {
                val argb = it.toArgb()
                window.navigationBarColor = argb
                window.statusBarColor = argb
            }
            val insetsController = window.decorView.let(ViewCompat::getWindowInsetsController)
            insetsController?.let {
                it.apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun NavigationBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun StatusBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}