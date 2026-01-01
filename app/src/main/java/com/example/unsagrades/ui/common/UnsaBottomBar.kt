package com.example.unsagrades.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.unsagrades.ui.navigation.BottomNavItem
import com.example.unsagrades.ui.theme.UNSAGradesTheme

@Composable
fun UnsaBottomBar(
    navController: NavController,
    currentFabIcon: ImageVector,
    onFabClick: () -> Unit,
    testRoute: String? = null
) {
    val items = listOf(
        BottomNavItem.Profile,
        BottomNavItem.History,
        // BottomNavItem.Settings // Descomenta cuando tengas la pantalla de ajustes
    )

    // Observamos la ruta actual para saber qué ícono iluminar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // Función auxiliar para saber si un ítem está seleccionado
    fun isSelected(route: String): Boolean {
        return testRoute?.startsWith(route.substringBefore("?"))
            ?: (currentRoute?.startsWith(route.substringBefore("?")) == true)
    }

    // Usamos BottomAppBar para tener control total del layout
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), // Altura total (Barra 64dp + Espacio para sobresalir)
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            contentColor = MaterialTheme.colorScheme.onSurface, // Texto gris
            tonalElevation = 8.dp, // Sombra suave para que destaque
            modifier = Modifier.height(80.dp) // Altura estándar cómoda
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                // Distribuimos el espacio equitativamente entre los 3 elementos
                horizontalArrangement = Arrangement.SpaceEvenly, //SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. PERFIL
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BoxedIconButton(
                        item = BottomNavItem.Profile,
                        isSelected = isSelected(BottomNavItem.Profile.route),
                        onClick = { navigateTo(navController, BottomNavItem.Profile.route) }
                    )
                }

                // 2. BOTÓN CENTRAL (FAB)
                // Integrado en la fila, sin offsets raros.
                FloatingActionButton(
                    onClick = onFabClick,
                    containerColor = if (isSelected(BottomNavItem.Dashboard.route)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    ),
                    shape = CircleShape, // O RoundedCornerShape(16.dp) si prefieres
                    // Tamaño: 64dp (un poco más grande que lo estándar de 56dp para destacar)
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        imageVector = currentFabIcon,
                        //tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Acción Principal",
                        modifier = Modifier.size(32.dp),
                    )
                }

                // 3. HISTORIAL
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BoxedIconButton(
                        item = BottomNavItem.History,
                        isSelected = isSelected(BottomNavItem.History.route),
                        onClick = { navigateTo(navController, BottomNavItem.History.route) }
                    )
                }
            }
        }
    }
}


@Composable
    fun BoxedIconButton(
    item: BottomNavItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape) // Recorta el fondo en círculo perfecto
            .size(40.dp) // Tamaño del área táctil y del círculo de fondo
            .background(
                // Fondo Morado Intenso si está seleccionado, Transparente si no
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            ),
        colors = IconButtonDefaults.iconButtonColors(
            // Icono Blanco si está seleccionado (para contraste), Gris si no
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
        )
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = if(isSelected) Modifier.size(22.dp) else Modifier.size(28.dp)
        )
    }
}

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { startRoute ->
            popUpTo(startRoute) { saveState = true }
        }
        launchSingleTop = true
        restoreState = true
    }
}

//    NavigationBar(


@Preview(showBackground = true, widthDp = 450)
@Composable
fun UnsaBottomBarPreview_ProfileSelected() {
    // 1. Create a fake NavController (works in Previews)
    val navController = rememberNavController()

    UNSAGradesTheme(dynamicColor = false) {
        UnsaBottomBar(
            navController = navController,
            currentFabIcon = Icons.Default.Add, // Placeholder icon
            onFabClick = {},
            // 2. Force the UI to think we are on the Profile screen
            testRoute = BottomNavItem.Dashboard.route
        )
    }
}