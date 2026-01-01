package com.example.unsagrades.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Define los botones que aparecerán en el Footer (Bottom Navigation).
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // 1. Dashboard (Pantalla Principal)
    // Nota: Usamos la ruta base sin argumentos para el botón
    object Dashboard : BottomNavItem(
        route = Routes.Dashboard.createRoute(),
        title = "Inicio",
        icon = Icons.Default.Home
    )

    // 2. Historial (Tus semestres anteriores)
    object History : BottomNavItem(
        route = Routes.SemesterHistory.route,
        title = "Semestres",
        icon = Icons.Default.DateRange
    )

    // 3. Configuración (Opcional, pero se ve bien para balancear la barra)
    // Si aún no tenemos pantalla, podemos redirigir a una temporal o al inicio.
    object Settings : BottomNavItem(
        route = "settings_placeholder", // Ruta temporal
        title = "Ajustes",
        icon = Icons.Default.Settings
    )

    object Profile : BottomNavItem(
        route = Routes.Profile.route,
        title = "Perfil",
        icon = Icons.Default.AccountCircle
    )

}