package com.example.unsagrades.ui.feature.coursedetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.GradeType
import com.example.unsagrades.data.local.relation.CourseWithConfigAndGrades
import com.example.unsagrades.domain.repository.GradeRepository
import com.example.unsagrades.domain.usecase.CalculateWeightedAverageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// Estado de UI complejo para esta pantalla
data class CourseDetailUiState(
    val courseName: String = "",
    val average: Double = 0.0,
    val status: String = "Cargando...",
    val partials: List<PartialUiModel> = emptyList(),
    val sustitutorio: GradeEntity? = null // Nota del susti si existe
)

data class PartialUiModel(
    val config: EvaluationConfigEntity,
    val continuousGrade: GradeEntity,
    val examGrade: GradeEntity
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val repository: GradeRepository,
    private val calculateAverageUseCase: CalculateWeightedAverageUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    // Estado principal
    val uiState: StateFlow<CourseDetailUiState> = repository.getCourseDetail(courseId)
        .combine(MutableStateFlow(0)) { courseData, _ ->
            if (courseData == null) {
                CourseDetailUiState(status = "Curso no encontrado")
            } else {
                mapToUiState(courseData)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CourseDetailUiState()
        )

    private fun mapToUiState(data: CourseWithConfigAndGrades): CourseDetailUiState {
        // 1. Aplanar todas las notas para la calculadora
        val allGrades = data.evaluations.flatMap { it.grades }
        val allConfigs = data.evaluations.map { it.config }

        // 2. Calcular promedio
        val avg = calculateAverageUseCase(allConfigs, allGrades)

        // 3. Determinar Estado
        val statusText = when {
            avg >= 10.5 -> "Aprobado"
            avg >= 10.0 -> "En Riesgo / Proyectado"
            else -> "Desaprobado"
        }

        // 4. Mapear Parciales (Asegurando que siempre haya Exam y Cont)
        val partialsList = data.evaluations.map { eval ->
            // Buscamos si existe la nota en DB, si no, creamos un placeholder (virtual)
            // OJO: El ID es random en el placeholder, pero al guardar usaremos insert/replace
            val existingCont = eval.grades.find { it.type == GradeType.CONTINUOUS }
            val existingExam = eval.grades.find { it.type == GradeType.EXAM }

            PartialUiModel(
                config = eval.config,
                continuousGrade = existingCont ?: GradeEntity(
                    configId = eval.config.id,
                    type = GradeType.CONTINUOUS,
                    value = 0,
                    isConfirmed = false
                ),
                examGrade = existingExam ?: GradeEntity(
                    configId = eval.config.id,
                    type = GradeType.EXAM,
                    value = 0,
                    isConfirmed = false
                )
            )
        }.sortedBy { it.config.partialNumber }

        // 5. Buscar Sustitutorio
        // El susti no tiene configId propia en la lista visual, así que lo buscamos por tipo en cualquier lado
        // O lo buscamos en una config especial si implementamos esa lógica. 
        // Para simplificar, asumiremos que el susti se guarda asociado a la config del Parcial 1 (o cualquiera) 
        // pero con tipo SUSTI, o mejor, lo buscamos en la lista plana.
        val susti = allGrades.find { it.type == GradeType.SUSTI }

        return CourseDetailUiState(
            courseName = data.course.name,
            average = avg,
            status = statusText,
            partials = partialsList,
            sustitutorio = susti
        )
    }

    // --- ACCIONES DE USUARIO ---

    fun onGradeChange(grade: GradeEntity, newValue: String) {
        // 1. Si el usuario borra todo, guardamos 0
        if (newValue.isEmpty()) {
            saveGrade(grade.copy(value = 0))
            return
        }

        // 2. Limpieza de ceros a la izquierda (ej: "05" -> "5")
        // Esto evita que al escribir "5" sobre un campo vacío se interprete mal si hubiera residuos
        val sanitizedValue = if (newValue.startsWith("0") && newValue.length > 1) {
            newValue.trimStart('0')
        } else {
            newValue
        }

        // 3. Validación numérica básica
        val valueInt = sanitizedValue.toIntOrNull() ?: return

        // 4. BLOQUEO ESTRICTO: Si intenta poner 21 o más, ignoramos el cambio.
        // Al hacer 'return', el estado no cambia y en la UI el carácter no se escribe.
        if (valueInt > 20) return

        // 5. Guardar
        saveGrade(grade.copy(value = valueInt))
    }

    fun onWeightChange(config: EvaluationConfigEntity, newValue: String, isExam: Boolean) {
        val valueInt = if(newValue.isEmpty()) 0 else newValue.toIntOrNull() ?: 0
        val finalValue = valueInt.coerceIn(0, 100)

        viewModelScope.launch {
            if (isExam){
                repository.updateConfig(config.copy(examWeight = finalValue.toFloat()))
            } else {
                repository.updateConfig(config.copy(continuousWeight = finalValue.toFloat()))
            }
        }
    }

    fun onGradeStep(grade: GradeEntity, step: Int) {
        val newValue = (grade.value + step).coerceIn(0, 20)
        saveGrade(grade.copy(value = newValue))
    }

    fun onConfirmationChange(grade: GradeEntity, isConfirmed: Boolean) {
        // Al confirmar, forzamos inclusión en promedio
        saveGrade(grade.copy(isConfirmed = isConfirmed, isIncludedInAverage = true))
    }

    fun onInclusionChange(grade: GradeEntity, isIncluded: Boolean) {
        saveGrade(grade.copy(isIncludedInAverage = isIncluded))
    }

    // --- LÓGICA SUSTITUTORIO ---

    fun addSustitutorio() {
        val currentState = uiState.value
        if (currentState.partials.isEmpty()) return

        // Asociamos el Susti a la primera configuración disponible (solo por FK, no afecta lógica)
        val firstConfigId = currentState.partials.first().config.id

        val newSusti = GradeEntity(
            configId = firstConfigId,
            type = GradeType.SUSTI,
            value = 0,
            isConfirmed = false
        )
        saveGrade(newSusti)
    }

    fun removeSustitutorio() {
        val susti = uiState.value.sustitutorio ?: return
        viewModelScope.launch {
            repository.deleteGrade(susti)
        }
    }

    fun onUpdateSusti(grade: GradeEntity, newValue: String) {
        val valueInt = newValue.toIntOrNull() ?: 0
        saveGrade(grade.copy(value = valueInt.coerceIn(0, 20)))
    }

    private fun saveGrade(grade: GradeEntity) {
        Log.d("UG", "Saving grade: $grade")
        viewModelScope.launch {
            repository.updateGrade(grade)
        }
    }
}