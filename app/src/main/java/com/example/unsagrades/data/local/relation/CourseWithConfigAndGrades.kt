package com.example.unsagrades.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.unsagrades.data.local.entity.CourseEntity
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity

/**
 * Esta clase representa la jerarquía completa de un Curso.
 * Room llenará esto automáticamente usando las relaciones definidas.
 * Estructura: Curso -> Lista[Configuración -> Lista[Notas]]
 */
data class CourseWithConfigAndGrades(
    @Embedded val course: CourseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "courseId",
        entity = EvaluationConfigEntity::class
    )
    val evaluations: List<EvaluationWithGrades>
)

data class EvaluationWithGrades(
    @Embedded val config: EvaluationConfigEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "configId",
        entity = GradeEntity::class
    )
    val grades: List<GradeEntity>
)