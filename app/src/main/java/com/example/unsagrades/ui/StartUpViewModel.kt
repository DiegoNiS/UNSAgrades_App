package com.example.unsagrades.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.dao.SemesterDao
import com.example.unsagrades.data.local.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StartUpState {
    object Loading : StartUpState()
    object NewUser : StartUpState() // Ir a Welcome
    object AlreadyRegisteredButNoSemester : StartUpState() // Ir a Dashboard
    //object OldUser: StartUpState() // Ir a crear usuario nueo y luego dashboard
    object ExistingUser : StartUpState() // Ir a Dashboard (o NewSemester si no hay activo)
}

@HiltViewModel
class StartUpViewModel @Inject constructor(
    private val userDao: UserDao,
    private val semesterDao: SemesterDao
) : ViewModel() {

    private val _startDestination = MutableStateFlow<StartUpState>(StartUpState.Loading)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            // Verificamos si existe el usuario con ID 1
            val user = userDao.getUserProfile().firstOrNull()
            val anySemester = semesterDao.getAllSemesters().firstOrNull()

            if (user != null) {
                if (anySemester.isNullOrEmpty()) {
                    _startDestination.value = StartUpState.AlreadyRegisteredButNoSemester
                } else {
                    _startDestination.value = StartUpState.ExistingUser
                }
            } else {
                _startDestination.value = StartUpState.NewUser
            }
        }
    }
}