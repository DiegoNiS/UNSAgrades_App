package com.example.unsagrades.ui.feature.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.entity.GradeType
import com.example.unsagrades.data.local.relation.CourseWithConfigAndGrades
import com.example.unsagrades.domain.repository.GradeRepository
import com.example.unsagrades.domain.usecase.CalculateWeightedAverageUseCase
import com.example.unsagrades.ui.common.CourseStatus
import com.example.unsagrades.ui.common.SortDirection
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




// 3. NUEVO: Estado completo del filtro
data class SortState(
    val type: SortType = SortType.STATUS,
    val direction: SortDirection = SortDirection.ASCENDING
)

// 2. ACTUALIZADO: Tipos de ordenamiento (+ Creación)
enum class SortType(val displayName: String) {
    CREATED("Creación"), // Por ID
    NAME("Nombre"),
    AVERAGE("Promedio"),
    STATUS("Estado")
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


    // 4. CAMBIO: Usamos el objeto SortState en lugar de solo el Enum
    private val _sortState = MutableStateFlow(SortState())
    val sortState = _sortState.asStateFlow()

    private val _rawCourseList = MutableStateFlow<List<CourseCardUiModel>>(emptyList())

    val semesterName = MutableStateFlow("Cargando...")

    val courseCards: StateFlow<List<CourseCardUiModel>> = combine(
        _rawCourseList,
        _sortState
    ) { list, state ->
        sortCourses(list, state)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            targetSemesterFlow.collect { semester ->
                if (semester != null) {
                    semesterName.value = semester.name

                    repository.getCoursesForSemester(semester.id).collect { rawCourses ->
                        // Guardamos la lista cruda mapeada
                        _rawCourseList.value = rawCourses.map { mapToUiModel(it) }
                    }
                } else {
                    semesterName.value = "Semestre no encontrado"
                    _rawCourseList.value = emptyList()
                }
            }
        }
    }

    // NUEVO: Función para cambiar el orden desde la UI

    // 5. LÓGICA DE TOGGLE INTELIGENTE
    fun onSortChange(newType: SortType) {
        val current = _sortState.value

        if (current.type == newType) {
            // Si clickeas el mismo, invierte la dirección (Toggle)
            val newDirection = if (current.direction == SortDirection.ASCENDING) {
                SortDirection.DESCENDING
            } else {
                SortDirection.ASCENDING
            }
            _sortState.value = current.copy(direction = newDirection)
        } else {
            // Si es uno nuevo, lo seleccionamos con un default lógico
            // Por defecto ASC (A-Z), excepto Promedio que suele buscarse DESC (Mayor a menor)
            val defaultDirection = if (newType == SortType.AVERAGE) SortDirection.DESCENDING else SortDirection.ASCENDING
            _sortState.value = SortState(type = newType, direction = defaultDirection)
        }
    }

    // Lógica de ordenamiento
    // 6. ALGORITMO DE ORDENAMIENTO DE DOBLE VÍA
    private fun sortCourses(list: List<CourseCardUiModel>, state: SortState): List<CourseCardUiModel> {
        val sortedList = when (state.type) {
            SortType.CREATED -> list.sortedBy { it.id } // Orden determinista por ID
            SortType.NAME -> list.sortedBy { it.name }
            SortType.AVERAGE -> list.sortedBy { it.average }
            SortType.STATUS -> list.sortedBy { it.status.priority }
        }

        return if (state.direction == SortDirection.DESCENDING) {
            sortedList.reversed()
        } else {
            sortedList
        }
    }

    private fun mapToUiModel(raw: CourseWithConfigAndGrades): CourseCardUiModel {
        // 1. Calcular promedio actual (Considerando notas hipotéticas y todo lo marcado)
        val grades = raw.evaluations.flatMap { it.grades }
        val average = calculateAverageUseCase(raw.evaluations.map { it.config }, grades)
        val susti = grades.find { it.type == GradeType.SUSTI }

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
        val confirmedEvaluations = grades.count { it.isConfirmed }
        val denominador = if (susti != null) 7.0 else 6.0
        val progress = (confirmedEvaluations / denominador).toFloat() // Placeholder: Aquí iría lógica matemática de pesos procesados

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

