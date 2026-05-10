package com.readora.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Ink = Color(0xFF0B0E16)
val InkRaised = Color(0xFF141927)
val Paper = Color(0xFFF7EDDD)
val Ember = Color(0xFFF9B17A)
val Coral = Color(0xFFFF7A59)
val Mint = Color(0xFFA7F97A)
val Sky = Color(0xFF7AD7F9)
val Gold = Color(0xFFFFD166)

/** Resolves a stored accent key to its [Color]. Falls back to [Ember]. */
fun accentColorFromKey(key: String): Color = when (key) {
    "mint"  -> Mint
    "sky"   -> Sky
    "coral" -> Coral
    "gold"  -> Gold
    else    -> Ember   // "ember" / "default"
}

/** Accent colour available throughout the composition tree. */
val LocalAccentColor = compositionLocalOf { Ember }

val ReadoraDarkScheme: ColorScheme = darkColorScheme(
    primary = Ember,
    onPrimary = Color(0xFF2B1607),
    secondary = Sky,
    onSecondary = Color(0xFF062432),
    tertiary = Mint,
    background = Ink,
    onBackground = Paper,
    surface = InkRaised,
    onSurface = Paper,
    surfaceVariant = Color(0xFF20283A),
    onSurfaceVariant = Color(0xFFD5C7B6),
    outline = Color(0xFF4B5368),
)

@Composable
fun ReadoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentKey: String = "default",
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val accent = accentColorFromKey(accentKey)
    val colorScheme = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicScheme = if (darkTheme) {
            dynamicDarkColorScheme(LocalView.current.context)
        } else {
            dynamicLightColorScheme(LocalView.current.context)
        }
        dynamicScheme.copy(primary = accent)
    } else {
        ReadoraDarkScheme.copy(primary = accent)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    CompositionLocalProvider(LocalAccentColor provides accent) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}
