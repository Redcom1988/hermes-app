package dev.redcom1988.hermes.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// LIGHT MODE
val FilamentLightColorScheme = lightColorScheme(
    primary = Color(0xFFf59e0b), // amber-500
    onPrimary = Color.White,
    primaryContainer = Color(0xFFfef3c7), // amber-100

    background = Color(0xFFF9FAFB), // gray-50
    onBackground = Color(0xFF111827), // gray-900

    surface = Color.White,
    onSurface = Color(0xFF1F2937), // gray-800

    outline = Color(0xFFD1D5DB), // gray-300
)

// DARK MODE
val FilamentDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFBBF24), // amber-400
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF422006), // even deeper amber-950

    background = Color(0xFF0A0A0A), // near-black
    onBackground = Color(0xFFF3F4F6), // light gray

    surface = Color(0xFF18181B), // very dark gray
    onSurface = Color(0xFFE5E7EB), // light gray

    outline = Color(0xFF374151), // darker gray-700
)