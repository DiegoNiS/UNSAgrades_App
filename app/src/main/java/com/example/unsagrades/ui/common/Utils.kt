package com.example.unsagrades.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ConfigurarBarraEstado(
    color: Color,
    iconosOscuros: Boolean // true = iconos negros, false = iconos blancos
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 1. Cambiamos el color de fondo de la barra
            window.statusBarColor = color.toArgb()

            // 2. Controlamos si los iconos (batería, hora) son blancos o negros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = iconosOscuros
        }
    }
}