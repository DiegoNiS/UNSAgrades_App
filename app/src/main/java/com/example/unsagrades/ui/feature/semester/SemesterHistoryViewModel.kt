package com.example.unsagrades.ui.feature.semester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.domain.repository.GradeRepository
import com.example.unsagrades.domain.usecase.CalculateWeightedAverageUseCase
import com.example.unsagrades.ui.common.SortDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SemesterHistoryUiModel(
    val id: String,
    val name: String,
    val approvedCount: Int,
    val failedCount: Int,
    val average: Double,
    val message: String,
    val isCurrent: Boolean
)

// --- LÓGICA DE ORDENAMIENTO ---

enum class HistorySortType(val displayName: String) {
    NAME("Nombre"),       // Cronológico
    AVERAGE("Promedio"),  // Rendimiento
    FAILED("Reprobados")  // Dificultad
}

data class HistorySortState(
    val type: HistorySortType = HistorySortType.NAME,
    val direction: SortDirection = SortDirection.ASCENDING
)

@HiltViewModel
class SemesterHistoryViewModel @Inject constructor(
    private val repository: GradeRepository,
    private val calculateAverageUseCase: CalculateWeightedAverageUseCase
) : ViewModel() {

    // Lista cruda (sin ordenar)
    private val _rawHistoryList = MutableStateFlow<List<SemesterHistoryUiModel>>(emptyList())

    // Estado del ordenamiento
    private val _sortState = MutableStateFlow(HistorySortState())
    val sortState = _sortState.asStateFlow()

    // Lista final procesada (Combinación de datos + orden)
    val uiState: StateFlow<List<SemesterHistoryUiModel>> = combine(
        _rawHistoryList,
        _sortState
    ) { list, sort ->
        sortHistory(list, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadHistory()
    }

    fun onSortChange(newType: HistorySortType) {
        val current = _sortState.value

        if (current.type == newType) {
            // Toggle dirección
            val newDirection = if (current.direction == SortDirection.ASCENDING) {
                SortDirection.DESCENDING
            } else {
                SortDirection.ASCENDING
            }
            _sortState.value = current.copy(direction = newDirection)
        } else {
            // Nuevo tipo -> Default lógico
            // Promedio y Reprobados usualmente queremos ver el mayor primero (DESC)
            val defaultDirection = if (newType == HistorySortType.AVERAGE || newType == HistorySortType.FAILED) {
                SortDirection.DESCENDING
            } else {
                SortDirection.ASCENDING
            }
            _sortState.value = HistorySortState(type = newType, direction = defaultDirection)
        }
    }

    private fun sortHistory(list: List<SemesterHistoryUiModel>, state: HistorySortState): List<SemesterHistoryUiModel> {
        val sortedList = when (state.type) {
            HistorySortType.NAME -> list.sortedBy { it.name }
            HistorySortType.AVERAGE -> list.sortedBy { it.average }
            HistorySortType.FAILED -> list.sortedBy { it.failedCount }
        }

        return if (state.direction == SortDirection.DESCENDING) {
            sortedList.reversed()
        } else {
            sortedList
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.getAllSemesters().collect { semesters ->
                val historyList = semesters.map { semester ->
                    val coursesWithGrades = repository.getCoursesForSemester(semester.id).first()

                    var sumAverages = 0.0
                    var approved = 0
                    var failed = 0

                    coursesWithGrades.forEach { courseData ->
                        val allGrades = courseData.evaluations.flatMap { it.grades }
                        val allConfigs = courseData.evaluations.map { it.config }

                        val avg = calculateAverageUseCase(allConfigs, allGrades)
                        sumAverages += avg

                        if (avg >= 10.5) approved++ else failed++
                    }

                    val semesterAvg = if (coursesWithGrades.isNotEmpty()) {
                        sumAverages / coursesWithGrades.size
                    } else {
                        0.0
                    }

                    val msg = when {
                        failed > 0 -> "Hubo problemas al desaprobar $failed curso(s)"
                        semesterAvg > 17 -> "¡Excelente semestre, notas sobresalientes!"
                        semesterAvg >= 14 -> "Buen semestre, buenas notas"
                        semesterAvg >= 10.5 -> "Semestre regular, se logró aprobar"
                        else -> "Semestre complicado, a mejorar"
                    }

                    SemesterHistoryUiModel(
                        id = semester.id,
                        name = semester.name,
                        approvedCount = approved,
                        failedCount = failed,
                        average = semesterAvg,
                        message = msg,
                        isCurrent = semester.isCurrent
                    )
                }
                // Guardamos en la lista cruda para que el 'combine' haga el resto
                _rawHistoryList.value = historyList
            }
        }
    }
}