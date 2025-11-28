package com.example.unsagrades.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "evaluation_configs",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class EvaluationConfigEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val courseId: String,
    val partialNumber: Int, // 1, 2, 3...
    val examWeight: Float, // Ej: 0.6 (60%)
    val continuousWeight: Float // Ej: 0.4 (40%)
)