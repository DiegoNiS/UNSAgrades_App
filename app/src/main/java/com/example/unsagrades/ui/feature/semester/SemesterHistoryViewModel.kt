package com.example.unsagrades.ui.feature.semester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.domain.repository.GradeRepository
import com.example.unsagrades.domain.usecase.CalculateWeightedAverageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

@HiltViewModel
class SemesterHistoryViewModel @Inject constructor(
    private val repository: GradeRepository,
    private val calculateAverageUseCase: CalculateWeightedAverageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<SemesterHistoryUiModel>>(emptyList())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            // 1. Obtenemos todos los semestres
            repository.getAllSemesters().collect { semesters ->
                val historyList = semesters.map { semester ->

                    // 2. Para cada semestre, obtenemos sus cursos (Snapshot de una sola vez)
                    // Usamos .first() para obtener la lista actual sin quedarnos escuchando cambios
                    val coursesWithGrades = repository.getCoursesForSemester(semester.id).first()

                    var sumAverages = 0.0
                    var approved = 0
                    var failed = 0

                    // 3. Calculamos estadísticas
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

                    // 4. Generamos el mensaje dinámico
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
                _uiState.value = historyList
            }
        }
    }
}