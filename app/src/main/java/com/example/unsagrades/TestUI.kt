package com.example.unsagrades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun RandomMeshBackground() {
    // 1. Obtenemos el tamaño de la pantalla para distribuir los colores
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // 2. Definimos una paleta de colores vibrantes para mezclar
    val palette = listOf(
        Color(0xFFF59174), Color(0xFFA778EF),
        Color(0xFF6FE3D7), Color(0xFFEFD06F),
        Color(0xFFE87297), Color(0xFF76B6EE)
    )

    // 3. Generamos "manchas" de color aleatorias.
    // Usamos 'remember' para que no cambien en cada recomposición (parpadeo),
    // a menos que tú quieras que se muevan.
    val randomCircles = remember {
        List(5) { // Crearemos 5 manchas de color
            RandomCircleData(
                // Seleccionamos un color aleatorio de la paleta, extraemos el elemento de la lista y la reordenamos
                color = palette.random(),
                size = Random.nextInt(200, 300).dp,
                offsetX = Random.nextInt(200, screenWidth.value.toInt() - 200).dp,
                offsetY = Random.nextInt(200, screenHeight.value.toInt() - 200).dp
            )
        }
    }

    // 4. El contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Color base de fondo
    ) {
        // Renderizamos cada mancha
        randomCircles.forEach { circle ->
            Box(
                modifier = Modifier
                    .offset(x = circle.offsetX, y = circle.offsetY)
                    .size(circle.size)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(circle.color.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
                    // El truco está en el BLUR alto para que se mezclen suavemente
                    .blur(radius = 260.dp)
            )
        }

        // Aquí iría tu contenido real (Column, textos, botones, etc.)
        // Box(modifier = Modifier.fillMaxSize()) { ... }
    }
}

// Clase de datos auxiliar para guardar la posición aleatoria
data class RandomCircleData(
    val color: Color,
    val size: Dp,
    val offsetX: Dp,
    val offsetY: Dp
)

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun RandomMeshBackgroundPreview() {
    RandomMeshBackground()
}