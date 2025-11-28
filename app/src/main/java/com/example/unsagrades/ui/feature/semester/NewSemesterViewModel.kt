package com.example.unsagrades.ui.feature.semester

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.entity.SemesterEntity
import com.example.unsagrades.domain.repository.GradeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewSemesterViewModel @Inject constructor(
    private val repository: GradeRepository
) : ViewModel() {

    // Estado del formulario (Nombre y Switch)
    private val _semesterName = MutableStateFlow("")
    val semesterName = _semesterName.asStateFlow()

    private val _isCurrent = MutableStateFlow(true) // Por defecto true, como en tu diseño
    val isCurrent = _isCurrent.asStateFlow()

    // NUEVO: Estado para mensajes de error (ej. "Nombre repetido")
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Eventos de una sola vez (Navegación)
    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onNameChange(newName: String) {
        _semesterName.value = newName
    }

    fun onIsCurrentChange(isChecked: Boolean) {
        _isCurrent.value = isChecked
    }

    fun saveSemester() {
        val nameToSave = _semesterName.value.trim() // Quitamos espacios en blanco extra
        if (nameToSave.isBlank()) {
            _errorMessage.value = "Ingresa un nombre válido"
            return
        }

        viewModelScope.launch {
            // 1. VALIDACIÓN IMPORTANTE: ¿Ya existe el nombre?
            val existingSemester = repository.getSemesterByName(nameToSave)

            if (existingSemester != null) {
                // Si existe, detenemos todo y mostramos error
                _errorMessage.value = "El semestre '$nameToSave' ya existe en tu historial."
                return@launch
            }

            // 2. Si no existe, procedemos a guardar
            val newSemester = SemesterEntity(
                name = nameToSave,
                isCurrent = _isCurrent.value
            )

            // Usamos la transacción inteligente que creamos en el DAO
            if (_isCurrent.value) {
                repository.setAsCurrentSemester(newSemester)
            } else {
                repository.saveSemester(newSemester)
            }

            // Avisamos a la UI que ya terminamos para que navegue
            _navigationEvent.emit(Unit)
        }
    }
}