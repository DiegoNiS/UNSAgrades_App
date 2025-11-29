package com.example.unsagrades.domain.usecase

import android.util.Log
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.GradeType
import javax.inject.Inject

/**
 * Calculadora Maestra (Versión Pesos Acumulativos Globales)
 * * LÓGICA ACTUALIZADA:
 * Este caso de uso asume que los pesos vienen validados globalmente por [ValidateCourseConfigUseCase].
 * Es decir, la suma de pesos de TODOS los parciales debe ser 100% (1.0).
 * * Fórmula:
 * Promedio = Sumatoria(Nota * PesoGlobal) / Sumatoria(PesosProcesados)
 * * Ejemplo:
 * Parcial 1 (Examen 15%, Continua 15%) -> Total 30% del curso.
 * Si tienes 20 en el examen: Puntos ganados = 20 * 0.15 = 3 puntos directos al promedio final.
 */
class CalculateWeightedAverageUseCase @Inject constructor(
    private val applySustitutorioUseCase: ApplySustitutorioUseCase
) {

    operator fun invoke(
        configs: List<EvaluationConfigEntity>,
        grades: List<GradeEntity>
    ): Double {
        if (configs.isEmpty()) return 0.0
        Log.d("UG", "cambio de notas registrado")

        // Mapa auxiliar para saber el número de parcial de cada nota rápidamente
        // Key: configId, Value: partialNumber (1, 2, 3...)
        val configMap = configs.associate { it.id to it.partialNumber }

        // 1. Separar notas regulares y nota de susti
        val regularGrades = grades.filter { it.type != GradeType.SUSTI }
        val sustiGrade = grades.find { it.type == GradeType.SUSTI }

        // 2. Identificar notas de tipo EXAMEN para ver si aplica el susti
        val allExamGrades = regularGrades.filter { it.type == GradeType.EXAM }.toMutableList()

        // --- CORRECCIÓN AQUÍ ---
        // Filtramos solo los exámenes elegibles para sustitución (Parcial 1 y 2)
        val eligibleForSusti = allExamGrades.filter {
            val pNum = configMap[it.configId] ?: 99
            pNum == 1 || pNum == 2
        }.toMutableList()

        // Los demás (Parcial 3, etc.) los guardamos aparte, son intocables
        val protectedExams = allExamGrades.filter {
            val pNum =  configMap[it.configId] ?: 99
            pNum != 1 && pNum != 2
        }

        // 3. Aplicar lógica de sustitutorio SOLO a los elegibles
        // (Modifica la lista eligibleForSusti in-situ si aplica el reemplazo)
        if (sustiGrade != null) {
            applySustitutorioUseCase(eligibleForSusti, sustiGrade)
        }

        // 4. Reconstruir la lista completa de notas para procesar
        // Juntamos: Exámenes (posiblemente modificados) + Exámenes intocables + Continuas
        val continuousGrades = regularGrades.filter { it.type == GradeType.CONTINUOUS }

        val finalGradesToProcess = eligibleForSusti + protectedExams + continuousGrades

        // Agrupamos por configId para saber a qué parcial pertenece cada nota
        val gradesByConfig = finalGradesToProcess.groupBy { it.configId }

        var totalPoints = 0.0      // Puntos acumulados (ej. 4.5 puntos)
        //var processedWeight = 0.0  // Peso acumulado procesado (ej. 0.30 o 30%)

        for (config in configs) {
            val partialGrades = gradesByConfig[config.id] ?: emptyList()

            val exam = partialGrades.find { it.type == GradeType.EXAM }
            val continuous = partialGrades.find { it.type == GradeType.CONTINUOUS }

            // Normalización de pesos:
            // Si el usuario guardó "15" (entero), lo pasamos a "0.15". Si guardó "0.15", se queda así.
            val rawWExam = config.examWeight
            val rawWCont = config.continuousWeight

            val wExam = rawWExam / 100.0
            val wCont = rawWCont / 100.0

            // LÓGICA DE SUMA DIRECTA (Pesos Globales):

            // --- EXAMEN ---
            if (exam != null && exam.isIncludedInAverage) {
                totalPoints += (exam.value * wExam)
                Log.d("UG", "El valor del examen es: ${exam.value}")
                //processedWeight += wExam
            }

            // --- CONTINUA ---
            if (continuous != null && continuous.isIncludedInAverage) {
                totalPoints += (continuous.value * wCont)
                //processedWeight += wCont
            }
        }

        // Si no hemos procesado nada aún, el promedio es 0
        //if (processedWeight == 0.0) return 0.0

        // CÁLCULO FINAL:
        // Divide los puntos ganados entre el peso que ya se "jugó".
        // Si ya se jugó el 100% del curso (processedWeight = 1.0), es una suma directa.
        // Si solo va el 50% (processedWeight = 0.5), proyecta la nota a escala vigesimal.
        return totalPoints /// processedWeight
    }
}