package com.example.unsagrades.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE // Si borras el semestre, adiós cursos
        )
    ],
    // Indexar la FK hace que las consultas sean mucho más rápidas
    indices = [Index(value = ["semesterId"])]
)
data class CourseEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val semesterId: String, // Relación con el Semestre
    val name: String // Ej: "Inteligencia Artificial"
)