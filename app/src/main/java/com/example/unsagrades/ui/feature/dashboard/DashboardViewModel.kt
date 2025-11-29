package com.example.unsagrades.ui.feature.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.relation.CourseWithConfigAndGrades
import com.example.unsagrades.domain.repository.GradeRepository
import com.example.unsagrades.domain.usecase.CalculateWeightedAverageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.launch

// Estado de UI para un Card de Curso individual
data class CourseCardUiModel(
    val id: String,
    val name: String,
    val average: Double,
    val progress: Float, // 0.0 a 1.0 (Porcentaje de avance del curso)
    val status: CourseStatus
)

enum class CourseStatus {
    PASSING,   // Verde: Ya aprobó (> 10.5 real)
    PROJECTED, // Amarillo: Aprueba con notas hipotéticas/no confirmadas
    FAILING,   // Rojo: Aún no aprueba
    UNKNOWN
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: GradeRepository,
    private val calculateAverageUseCase: CalculateWeightedAverageUseCase,
    savedStateHandle: SavedStateHandle // Inyectamos esto para leer argumentos de navegación
) : ViewModel() {

    // 1. Obtenemos el semestre actual
    // 1. Leemos el ID del argumento (puede ser nulo)
    private val semesterId: String? = savedStateHandle["semesterId"]

    // 2. Lógica de decisión: ¿Qué semestre observamos?
    private val targetSemesterFlow = if (semesterId != null) {
        repository.getSemesterById(semesterId) // Vemos uno específico (Historial)
    } else {
        repository.getCurrentSemester() // Vemos el actual (Default)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        targetSemesterFlow,
        repository.getAllSemesters() // Usado solo como trigger de actualización
    ) { semester, _ ->
        if (semester == null) {
            DashboardUiState.Empty
        } else {
            DashboardUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )

    // NOTA: Para simplificar el combine y no complicar la consulta, 
    // hacemos un "flatMapLatest" manual observando el semestre actual.
    // En una app real production-grade usaríamos flatMapLatest de Flow.

    // Mejor aproximación simplificada para este MVP:
    // Exponemos el nombre del semestre y la lista de cursos calculados por separado.

    val semesterName = MutableStateFlow("Cargando...")

    private val _courseCards = MutableStateFlow<List<CourseCardUiModel>>(emptyList())
    val courseCards = _courseCards.asStateFlow()

    init {
        viewModelScope.launch {
            // Observamos el semestre decidido arriba
            targetSemesterFlow.collect { semester ->
                if (semester != null) {
                    semesterName.value = semester.name

                    // Cargamos los cursos de ese semestre
                    repository.getCoursesForSemester(semester.id).collect { rawCourses ->
                        _courseCards.value = rawCourses.map { mapToUiModel(it) }
                    }
                } else {
                    semesterName.value = "Semestre no encontrado"
                    _courseCards.value = emptyList()
                }
            }
        }
    }

    private fun mapToUiModel(raw: CourseWithConfigAndGrades): CourseCardUiModel {
        // 1. Calcular promedio actual (Considerando notas hipotéticas y todo lo marcado)
        val average = calculateAverageUseCase(raw.evaluations.map { it.config }, raw.evaluations.flatMap { it.grades })

        // 2. Calcular estado (Lógica simplificada para MVP)
        // En una v2 podríamos calcular "Promedio Real" vs "Promedio Proyectado" por separado.
        // Por ahora, asumimos que el cálculo incluye lo que el usuario marcó con el check.
        val status = when {
            average >= 10.5 -> CourseStatus.PASSING
            average >= 10.0 -> CourseStatus.PROJECTED // Margen de esperanza
            else -> CourseStatus.FAILING
        }

        // 3. Calcular progreso (cuánto del peso total ya tiene nota)
        // Esto es visual para la barra azul
        // Simplificación: Asumimos que cada nota registrada suma progreso.
        val confirmedEvaluations = raw.evaluations.flatMap { it.grades }.count { it.isConfirmed }
        val progress = (confirmedEvaluations / 6.0).toFloat() // Placeholder: Aquí iría lógica matemática de pesos procesados

        return CourseCardUiModel(
            id = raw.course.id,
            name = raw.course.name,
            average = average,
            progress = progress,
            status = status
        )
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    object Empty : DashboardUiState()
    data class Success(val semesterName: String, val courses: List<CourseCardUiModel>) : DashboardUiState()
}

