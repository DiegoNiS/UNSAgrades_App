package com.example.unsagrades.ui.common

enum class CourseStatus(val priority: Int) {
    FAILING(1),   // Prioridad alta (Rojo)
    PROJECTED(2), // Prioridad media (Amarillo)
    PASSING(3),   // Prioridad baja (Verde)
    UNKNOWN(4)
}