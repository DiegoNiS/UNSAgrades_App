package com.example.unsagrades.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// Definimos solo el esquema de luz usando tus colores
private val LightColorScheme = lightColorScheme(
    primary = UnsaPurple,
    secondary = UnsaPurpleLight,
    tertiary = Pink80,
    background = BackgroundWhite,
    surface = BackgroundWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextBlack,
    onSurface = TextBlack,
    error = StateFailingText,
    //containerColor = ItemBackground
)

@Composable
fun UNSAGradesTheme(
    // Aunque el sistema diga "true" (Modo Oscuro), aquí lo ignoraremos.
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivamos colores dinámicos (Android 12+) para mantener la identidad UNSA
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // FORZADO: Siempre usamos LightColorScheme
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 1. Pintamos la barra de estado de Morado UNSA
            window.statusBarColor = UnsaPurple.toArgb()

            // 2. Controlamos el color de los íconos (Hora, Batería)
            // false = Íconos Blancos (Para fondo oscuro como el morado)
            // true = Íconos Negros (Si la barra fuera blanca)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Necesitas el archivo Type.kt (ver abajo)
        content = content
    )
}