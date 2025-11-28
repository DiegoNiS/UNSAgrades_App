package com.example.unsagrades.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// Importamos tus pantallas reales
import com.example.unsagrades.ui.feature.semester.NewSemesterScreen
import com.example.unsagrades.ui.feature.dashboard.DashboardScreen
import com.example.unsagrades.ui.feature.createcourse.AddCourseScreen
import com.example.unsagrades.ui.feature.coursedetail.CourseDetailScreen
import com.example.unsagrades.ui.feature.semester.SemesterHistoryScreen


@Composable
fun UnsaNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.NewSemester.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. NUEVO SEMESTRE
        composable(Routes.NewSemester.route) {
            NewSemesterScreen(
                onNavigateToDashboard = {
                    navController.navigate(Routes.Dashboard.createRoute()) {
                        popUpTo(Routes.NewSemester.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. DASHBOARD (Soporta argumentos)
        composable(
            route = Routes.Dashboard.route,
            arguments = listOf(
                navArgument("semesterId") {
                    type = NavType.StringType
                    nullable = true // Opcional
                    defaultValue = null
                }
            )
        ) {
            DashboardScreen(
                onNavigateToCreateCourse = { navController.navigate(Routes.CreateCourse.route) },
                onNavigateToCourseDetail = { id -> navController.navigate(Routes.CourseDetail.createRoute(id)) },
                onNavigateToHistory = { navController.navigate(Routes.SemesterHistory.route) }
            )
        }

        // 3. CREAR CURSO
        composable(Routes.CreateCourse.route) {
            AddCourseScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate(Routes.CourseDetail.createRoute(id)) {
                        popUpTo(Routes.CreateCourse.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. DETALLE CURSO
        composable(
            route = Routes.CourseDetail.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) {
            CourseDetailScreen(onNavigateBack = { navController.popBackStack() })
        }

        // 5. HISTORIAL (Con navegación al Dashboard específico)
        composable(Routes.SemesterHistory.route) {
            SemesterHistoryScreen(
                onNavigateToNewSemester = { navController.navigate(Routes.NewSemester.route) },
                onSemesterClick = { semesterId ->
                    // AL CLICKAR, VAMOS AL DASHBOARD DE ESE SEMESTRE
                    navController.navigate(Routes.Dashboard.createRoute(semesterId))
                }
            )
        }
    }
}
//
//// Composable temporal para lo que falta
//@Composable
//fun PlaceholderScreen(title: String) {
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text(text = title)
//    }
//}