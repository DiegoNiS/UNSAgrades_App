package com.example.unsagrades.domain.usecase

import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.GradeType
import javax.inject.Inject

class ApplySustitutorioUseCase @Inject constructor() {

    /**
     * Recibe una lista mutable de notas de exámenes y la nota del susti.
     * Modifica la lista in-situ reemplazando la menor nota si aplica.
     */
    operator fun invoke(examGrades: MutableList<GradeEntity>, sustiGrade: GradeEntity) {
        // Solo aplicamos si el susti está confirmado o incluido en el cálculo
        if (!sustiGrade.isIncludedInAverage) return

        // Buscamos la nota de examen más baja
        val lowestExam = examGrades.minByOrNull { it.value } ?: return

        // Regla UNSA: El susti reemplaza a la nota más baja SOLO si es mayor que ella.
        if (sustiGrade.value > lowestExam.value) {
            val index = examGrades.indexOf(lowestExam)
            if (index != -1) {
                // Creamos una copia virtual de la nota reemplazada para el cálculo
                // Mantenemos el mismo configId para que se promedie en el parcial correcto
                examGrades[index] = lowestExam.copy(
                    value = sustiGrade.value,
                    isConfirmed = sustiGrade.isConfirmed, // Hereda estado del susti
                    type = GradeType.EXAM // Se disfraza de examen
                )
            }
        }
    }
}