package com.example.unsagrades.ui.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.dao.UserDao
import com.example.unsagrades.data.local.entity.UserEntity
import com.example.unsagrades.domain.repository.GradeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WelcomeNavigationEvent {
    object NavigateToNewSemester : WelcomeNavigationEvent()
    object NavigateToDashboard : WelcomeNavigationEvent()
}

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userDao: UserDao,
    private val repository: GradeRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<WelcomeNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun saveProfile(name: String, avatarId: Int) {
        viewModelScope.launch {
            // 1. Guardar perfil
            userDao.saveUserProfile(UserEntity(name = name, avatarId = avatarId))

            // 2. Verificar si existen semestres
            val semesters = repository.getAllSemesters().firstOrNull()

            // 3. Decidir navegación
            if (semesters.isNullOrEmpty()) {
                _navigationEvent.emit(WelcomeNavigationEvent.NavigateToNewSemester)
            } else {
                _navigationEvent.emit(WelcomeNavigationEvent.NavigateToDashboard)
            }
        }
    }
}