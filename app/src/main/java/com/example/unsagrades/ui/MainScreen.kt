package com.example.unsagrades.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.unsagrades.data.local.dao.UserDao
import com.example.unsagrades.ui.common.UnsaBottomBar
import com.example.unsagrades.ui.common.UnsaTopBar
import com.example.unsagrades.ui.navigation.Routes
import com.example.unsagrades.ui.navigation.UnsaNavGraph

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    startUpViewModel: StartUpViewModel = hiltViewModel()
) {
    val startState by startUpViewModel.startDestination.collectAsState()
    val navController = rememberNavController()

    if (startState is StartUpState.Loading) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        return
    }

    // Decidimos la ruta inicial
    val initialRoute = when (startState) {
        is StartUpState.NewUser -> Routes.Welcome.route
        is StartUpState.AlreadyRegisteredButNoSemester -> Routes.NewSemester.route

        // Si ya existe usuario, la lógica original era ir a NewSemester o Dashboard.
        // Por simplicidad, mandamos a NewSemester (que ya tiene lógica para redirigir si hay uno activo)
        else -> Routes.Dashboard.route
    }

    // Lógica para ocultar la barra en pantallas donde no tiene sentido
    // (Ej: No queremos ver el footer cuando estamos creando un semestre nuevo o editando un curso)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    // Detectar dónde estamos
    val isDashboard = currentRoute?.startsWith("dashboard") == true

    // El footer se muestra en estas 3 pantallas principales
    val isMainView = (isDashboard ||
            currentRoute == Routes.SemesterHistory.route ||
            currentRoute == "profile_screen")
            && currentRoute != Routes.Welcome.route

    // CONFIGURACIÓN DINÁMICA DEL BOTÓN CENTRAL
    val fabIcon = if (isDashboard) Icons.Default.Add else Icons.Default.Home

    val onFabClick: () -> Unit = {
        if (isDashboard) {
            // Acción 1: Agregar Curso
            navController.navigate(Routes.CreateCourse.route)
        } else {
            // Acción 2: Volver al Inicio (Dashboard)
            navController.navigate(Routes.Dashboard.createRoute()) {
                // Limpiamos el stack para volver al inicio limpio
                popUpTo(0)
            }
        }
    }


    Scaffold(
        topBar = {
            if (isMainView) {
                UnsaTopBar(userName = viewModel.userName)
            }
        },
        bottomBar = {
            if (isMainView) {
                UnsaBottomBar(
                    navController = navController,
                    currentFabIcon = fabIcon,
                    onFabClick = onFabClick
                )
            }
        }
    ) { innerPadding ->
        UnsaNavGraph(
            navController = navController,
            innerPadding = innerPadding,
            startDestination = initialRoute // <--- Pasamos la ruta calculada
        )
    }
}