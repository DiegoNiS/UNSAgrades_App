package com.example.unsagrades.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomProgressBar(
    progress: Float, // Valor actual entre 0.0f y 1.0f
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    colorProgreso: Color = MaterialTheme.colorScheme.secondary,
    colorFondo: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    cornerRadius: Dp = 8.dp
) {
    // 1. Animación suave del progreso
    val progresoAnimado by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "ProgresoAnimado"
    )

    // 2. Contenedor Principal (Track)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(colorFondo)
            // 3. Accesibilidad: Informamos al sistema qué es esto
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = progress.coerceIn(0f, 1f),
                    range = 0f..1f
                )
            }
    ) {
        // 4. Indicador de Progreso (Fill)
        Box(
            modifier = Modifier
                .fillMaxWidth(progresoAnimado)
                .fillMaxHeight()
                .background(colorProgreso)
        )
    }
}