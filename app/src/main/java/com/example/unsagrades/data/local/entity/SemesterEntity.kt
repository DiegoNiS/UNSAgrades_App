package com.example.unsagrades.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "semesters")
data class SemesterEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(), // ID único generado automáticamente
    val name: String, // Ej: "Quinto Semestre"
    val isCurrent: Boolean = false, // Solo uno debe ser true a la vez
    val isFinished: Boolean = false // Para saber si ya cerraste el ciclo
)