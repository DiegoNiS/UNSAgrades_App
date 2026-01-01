package com.example.unsagrades.ui.common

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RuedaInfinitaHorizontal(
    items: List<String>,
    initialIndex: Int = 0,
    visibleItemsCount: Int = 5,
    onSelectionChanged: (Int) -> Unit,
    tipographySelected: TextStyle = MaterialTheme.typography.titleMedium,
    tipographyUnselected: TextStyle = MaterialTheme.typography.titleSmall,
    colorSelected: Color = MaterialTheme.colorScheme.primary,
    colorUnselected: Color = MaterialTheme.colorScheme.onBackground,
    height: Dp = 24.dp
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val totalWidth = maxWidth
        val itemWidth = totalWidth / visibleItemsCount
        val itemWidthPx = with(LocalDensity.current) { itemWidth.toPx() }

        // 1. EL TRUCO DEL INFINITO
        // Empezamos en la mitad de Int.MAX_VALUE para que el usuario pueda scrollear
        // izquierda o derecha "infinitamente".
        val infiniteCount = Int.MAX_VALUE
        val middleOfList = infiniteCount / 2
        // Ajustamos para empezar exactamente en el 'initialIndex' pero en la zona media
        val startIndex = middleOfList - (middleOfList % items.size) + initialIndex

        val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
        val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

        // 2. DETECCIÓN DE SELECCIÓN LIMPIA (Sin padding que estorbe)
        LaunchedEffect(listState.isScrollInProgress) {
            if (!listState.isScrollInProgress) {
                val layoutInfo = listState.layoutInfo
                // El centro de la pantalla es simplemente la mitad del ancho del LazyRow
                val viewportCenter = layoutInfo.viewportEndOffset / 2

                val closestItem = layoutInfo.visibleItemsInfo.minByOrNull {
                    abs((it.offset + it.size / 2) - viewportCenter)
                }

                closestItem?.let {
                    // Usamos el operador % (Módulo) para obtener el índice real (0..20)
                    // a partir del índice infinito (ej. 1,500,023)
                    val realIndex = it.index % items.size
                    onSelectionChanged(realIndex)
                }
            }
        }

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            // 3. ¡ADIÓS PADDING! No lo necesitamos.
            modifier = Modifier.fillMaxWidth().height(height),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Usamos 'items' (con count numérico) en lugar de 'itemsIndexed'
            items(
                count = infiniteCount,
                key = { index -> index } // Clave única para rendimiento
            ) { index ->

                // Calculamos qué dato real toca mostrar
                val realIndex = index % items.size
                val itemText = items[realIndex]

                // 4. BOX CONTENEDOR (Igual que antes)
                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {

                    // Matemáticas visuales
                    val scaleFactor by remember {
                        derivedStateOf {
                            val layoutInfo = listState.layoutInfo
                            val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == index }

                            if (visibleItem != null) {
                                val viewportCenter = layoutInfo.viewportEndOffset / 2
                                val itemCenter = visibleItem.offset + (visibleItem.size / 2)
                                val distance = abs(viewportCenter - itemCenter)

                                val normalizedDistance = (distance / itemWidthPx).coerceIn(0f, 1f)
                                1.5f - (0.5f * normalizedDistance)
                            } else {
                                1f
                            }
                        }
                    }

                    // Umbral un poco más estricto para colorear solo el verdadero centro
                    val isSelected = scaleFactor > 1.45f

                    Text(
                        text = itemText,
                        style = if (isSelected) tipographySelected else tipographyUnselected,
                        textAlign = TextAlign.Center,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) colorSelected else colorUnselected,
                        modifier = Modifier
                            .scale(scaleFactor)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPrueba() {
    val numbers = (0..20).map { it.toString() }
    var selected by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Seleccionado: $selected", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(50.dp))

        RuedaInfinitaHorizontal(
            items = numbers,
            visibleItemsCount = 5, // Impar siempre ayuda visualmente
            initialIndex = 0,
            onSelectionChanged = { index ->
                selected = index
            }
        )
    }
}