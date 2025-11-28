package com.example.unsagrades.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "grades",
    foreignKeys = [
        ForeignKey(
            entity = EvaluationConfigEntity::class,
            parentColumns = ["id"],
            childColumns = ["configId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["configId"])]
)
data class GradeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val configId: String, // A qué parcial pertenece
    val type: GradeType,
    val value: Int, // Nota entera (0-20)
    val isConfirmed: Boolean = false, // True = Candado cerrado (Oficial)
    val isIncludedInAverage: Boolean = true // True = Checkbox marcado (Se cuenta en el promedio)
)