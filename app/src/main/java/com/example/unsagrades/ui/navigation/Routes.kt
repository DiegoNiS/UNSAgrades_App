package com.example.unsagrades.ui.navigation

/**
 * Define las rutas de navegación de la app.
 */
sealed class Routes(val route: String) {

    // Pantalla de inicio para crear/seleccionar semestre
    object NewSemester : Routes("new_semester")

    // Pantalla principal con los cards de cursos
    // CAMBIO: Ahora acepta un argumento opcional usando sintaxis de query (?param=value)
    object Dashboard : Routes("dashboard?semesterId={semesterId}") {
        fun createRoute(semesterId: String? = null): String {
            return if (semesterId != null) {
                "dashboard?semesterId=$semesterId"
            } else {
                "dashboard"
            }
        }
    }

    // Historial de semestres anteriores
    object SemesterHistory : Routes("semester_history")

    // Crear un nuevo curso
    // Podríamos pasar params, pero por simplicidad asumimos semestre actual
    object CreateCourse : Routes("create_course")

    // Detalle del curso (Notas)
    // Esta ruta requiere un argumento: courseId
    object CourseDetail : Routes("course_detail/{courseId}") {
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
}