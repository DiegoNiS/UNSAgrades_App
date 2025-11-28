package com.example.unsagrades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.domain.repository.GradeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainState {
    object Loading : MainState()
    data class Success(val startRoute: String) : MainState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GradeRepository
) : ViewModel() {

    private val _mainState = MutableStateFlow<MainState>(MainState.Loading)
    val mainState = _mainState.asStateFlow()

    init {
        viewModelScope.launch {
            val semesters = repository.getAllSemesters().first()
            if (semesters.isEmpty()) {
                _mainState.value = MainState.Success("new_semester")
            } else {
                _mainState.value = MainState.Success("dashboard")
            }
        }
    }
}
