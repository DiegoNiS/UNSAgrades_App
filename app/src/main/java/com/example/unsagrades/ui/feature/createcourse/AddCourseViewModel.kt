package com.example.unsagrades.ui.feature.createcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.entity.CourseEntity
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.domain.repository.GradeRepository
import com.example.unsagrades.domain.usecase.ValidateCourseConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

// Modelo temporal para la UI (antes de convertirlo a Entity)
data class PartialConfigUiModel(
    val number: Int,
    val examWeightStr: String = "", // String para facilitar el input de texto
    val continuousWeightStr: String = ""
)

@HiltViewModel
class AddCourseViewModel @Inject constructor(
    private val repository: GradeRepository,
    private val validateConfigUseCase: ValidateCourseConfigUseCase
) : ViewModel() {

    private val _courseName = MutableStateFlow("")
    val courseName = _courseName.asStateFlow()

    private val _partialConfigs = MutableStateFlow(
        listOf(
            PartialConfigUiModel(1),
            PartialConfigUiModel(2),
            PartialConfigUiModel(3)
        )
    )
    val partialConfigs = _partialConfigs.asStateFlow()

    // 1. New State for Total Weight
    private val _totalWeight = MutableStateFlow(0f)
    val totalWeight = _totalWeight.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Nuevo: Estado para controlar la visibilidad del diálogo de advertencia
    private val _showWeightWarningDialog = MutableStateFlow(false)
    val showWeightWarningDialog = _showWeightWarningDialog.asStateFlow()

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
        data class NavigateToDetail(val courseId: String) : NavigationEvent()
    }

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // Nuevo: para recordar la acción de navegación después de la confirmación del diálogo
    private var pendingGoToDetail: Boolean = false

    fun onNameChange(newName: String) {
        _courseName.value = newName
    }

    fun onWeightChange(index: Int, isExam: Boolean, newValue: String) {
        // Permitir números decimales
        if (newValue.isNotEmpty() && (!newValue.all { it.isDigit() || it == '.' } || newValue.count { it == '.' } > 1)) {
            return
        }

        val currentList = _partialConfigs.value.toMutableList()
        val item = currentList[index]

        currentList[index] = if (isExam) {
            item.copy(examWeightStr = newValue)
        } else {
            item.copy(continuousWeightStr = newValue)
        }
        _partialConfigs.value = currentList

        // 2. Recalculate total immediately after update
        recalculateTotalWeight(currentList)

        if (_errorMessage.value != null) _errorMessage.value = null
    }

    // 3. Private helper function to sum values
    private fun recalculateTotalWeight(configs: List<PartialConfigUiModel>) {
        val sum = configs.sumOf { item ->
            val exam = item.examWeightStr.toDoubleOrNull() ?: 0.0
            val continuous = item.continuousWeightStr.toDoubleOrNull() ?: 0.0
            exam + continuous
        }
        _totalWeight.value = sum.toFloat()
    }

    fun saveCourse(goToDetail: Boolean) {
        val name = _courseName.value.trim()
        if (name.isBlank()) {
            _errorMessage.value = "Ingresa un nombre para la asignatura"
            return
        }

        val tempConfigs = _partialConfigs.value.map { uiModel ->
            EvaluationConfigEntity(
                courseId = "temp",
                partialNumber = uiModel.number,
                examWeight = uiModel.examWeightStr.toFloatOrNull() ?: 0f,
                continuousWeight = uiModel.continuousWeightStr.toFloatOrNull() ?: 0f
            )
        }

        val validationResult = validateConfigUseCase(tempConfigs)
        if (validationResult is ValidateCourseConfigUseCase.Result.Invalid) {
            // En lugar de mostrar un error, activamos el diálogo
            pendingGoToDetail = goToDetail
            _showWeightWarningDialog.value = true
            return
        }

        // Si la validación es correcta, guardamos directamente
        proceedToSave(name, tempConfigs, goToDetail)
    }

    // Nuevo: El usuario confirma la advertencia
    fun onWeightWarningConfirmed() {
        _showWeightWarningDialog.value = false
        val name = _courseName.value.trim()
        val tempConfigs = _partialConfigs.value.map { uiModel ->
            EvaluationConfigEntity(
                courseId = "temp",
                partialNumber = uiModel.number,
                examWeight = uiModel.examWeightStr.toFloatOrNull() ?: 0f,
                continuousWeight = uiModel.continuousWeightStr.toFloatOrNull() ?: 0f
            )
        }
        // Procedemos a guardar usando la acción de navegación que habíamos guardado
        proceedToSave(name, tempConfigs, pendingGoToDetail)
    }

    // Nuevo: El usuario cancela el diálogo
    fun onWeightWarningDismissed() {
        _showWeightWarningDialog.value = false
    }

    private fun proceedToSave(name: String, tempConfigs: List<EvaluationConfigEntity>, goToDetail: Boolean) {
        viewModelScope.launch {
            try {
                // Usamos firstOrNull() para obtener el semestre actual de forma segura
                val currentSemester = repository.getCurrentSemester().firstOrNull()
                if (currentSemester == null) {
                    _errorMessage.value = "Error: No hay un semestre activo seleccionado."
                    return@launch
                }

                // 2. VALIDACIÓN: ¿Existe el curso en ESTE semestre?
                val existingCourse = repository.getCourseByName(name, currentSemester.id)
                if (existingCourse != null) {
                    _errorMessage.value = "La asignatura '$name' ya existe en este semestre."
                    return@launch
                }

                // 3. Guardar si todo está limpio
                val newCourse = CourseEntity(semesterId = currentSemester.id, name = name)
                val finalConfigs = tempConfigs.map { it.copy(courseId = newCourse.id) }

                repository.createCourseWithConfigs(newCourse, finalConfigs)

                if (goToDetail) {
                    _navigationEvent.emit(NavigationEvent.NavigateToDetail(newCourse.id))
                } else {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar el curso."
            }
        }
    }
}
