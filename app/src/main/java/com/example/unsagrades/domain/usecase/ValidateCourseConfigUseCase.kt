package com.example.unsagrades.domain.usecase

import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import javax.inject.Inject
import kotlin.math.abs

/**
 * Valida que la distribución de pesos de TODO el curso sea correcta.
 * Regla: La suma de (Peso Examen + Peso Continua) de TODOS los parciales debe ser 100% (o 1.0).
 */
class ValidateCourseConfigUseCase @Inject constructor() {

    sealed class Result {
        object Valid : Result()
        data class Invalid(val reason: String) : Result()
    }

    /**
     * Valida la lista completa de parciales configurados.
     * @param configs: Lista con la configuración de cada parcial (Ej: P1, P2, P3).
     */
    operator fun invoke(configs: List<EvaluationConfigEntity>): Result {
        var totalSum = 0f

        // Sumamos el peso de examen y continua de CADA parcial
        for (config in configs) {
            totalSum += config.examWeight + config.continuousWeight
        }

        // Validación: Aceptamos 100% (+-0.1) o 1.0 (+-0.01) para decimales
        val isPercentage = abs(totalSum - 100f) < 0.1f
        val isDecimal = abs(totalSum - 1.0f) < 0.01f

        return if (isPercentage || isDecimal) {
            Result.Valid
        } else {
            // Formateamos el mensaje para que sea claro si se pasó o le falta
            val formattedSum = if (totalSum > 2.0f) "$totalSum%" else "${totalSum * 100}%"
            Result.Invalid("La suma total de pesos es $formattedSum. Debe ser exactamente 100%.")
        }
    }
}