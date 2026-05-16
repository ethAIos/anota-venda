package com.caderninho.vendas.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightCaderninhoColors = lightColorScheme(
    primary = Green,
    onPrimary = Paper,
    primaryContainer = GreenSoft,
    onPrimaryContainer = Green,
    secondary = Ink,
    onSecondary = Paper,
    secondaryContainer = PaperAlt,
    onSecondaryContainer = Ink,
    tertiary = Amber,
    onTertiary = Paper,
    tertiaryContainer = Highlight,
    onTertiaryContainer = AmberInk,
    error = Red,
    onError = Paper,
    errorContainer = RedSoft,
    onErrorContainer = Red,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperAlt,
    onSurfaceVariant = InkSoft,
    outline = RuleStrong,
    outlineVariant = Rule,
    scrim = Ink,
)

@Composable
fun CaderninhoTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Paper.toArgb()
            window.navigationBarColor = Paper.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }
    CompositionLocalProvider(LocalCaveatStyle provides CaveatStyle) {
        MaterialTheme(
            colorScheme = LightCaderninhoColors,
            typography = CaderninhoTypography,
            shapes = CaderninhoShapes,
            content = content,
        )
    }
}
