package com.example.unsagrades.ui.feature.profile

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.R
import com.example.unsagrades.data.local.dao.UserDao
import com.example.unsagrades.data.local.entity.UserEntity
import com.example.unsagrades.domain.repository.GradeRepository
import com.example.unsagrades.domain.usecase.CalculateWeightedAverageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    @DrawableRes val avatar: Int = R.drawable.avatar_monster_1,
    val totalSemesters: Int = 0,
    val totalApprovedCourses: Int = 0,
    val totalFailedCourses: Int = 0,
    val careerAverage: Double = 0.0,
    val bestSemesterName: String = "-",
    val historyPoints: List<Double> = emptyList() // Para el gráfico (Promedio por semestre)
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GradeRepository,
    private val userDao: UserDao,
    private val calculateAverageUseCase: CalculateWeightedAverageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
        calculateCareerStats()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userDao.getUserProfile().collect { user ->
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        userName = user.name,
                        avatar = defineAvatar(user.avatarId)
                    )
                } else {
                    // Default si es nuevo
                    _uiState.value = _uiState.value.copy(userName = "Agustino")
                }
            }
        }
    }

    @DrawableRes
    private fun defineAvatar(avatarId: Int): Int = when (avatarId) {
        0 -> R.drawable.avatar_monster_1
        1 -> R.drawable.avatar_monster_2
        2 -> R.drawable.avatar_monster_3
        3 -> R.drawable.avatar_monster_4
        4 -> R.drawable.avatar_monster_5
        5 -> R.drawable.avatar_monster_6
        6 -> R.drawable.avatar_monster_7
        7 -> R.drawable.avatar_monster_8
        else -> R.drawable.avatar_monster_1
    }

    fun saveProfile(name: String, avatarId: Int) {
        viewModelScope.launch {
            userDao.saveUserProfile(UserEntity(name = name, avatarId = avatarId))
        }
    }

    private fun calculateCareerStats() {
        viewModelScope.launch {
            // 1. Obtenemos TODOS los semestres
            val semesters = repository.getAllSemesters().first() // Snapshot

            var totalApproved = 0
            var totalFailed = 0
            var sumSemesterAverages = 0.0
            val semesterAveragesList = mutableListOf<Double>()
            var bestAvg = -1.0
            var bestName = "-"

            // 2. Iteramos (Esto podría optimizarse en SQL, pero para <50 cursos está bien aquí)
            for (sem in semesters.sortedBy { it.name }) { // Ordenamos cronológicamente (aprox)
                val courses = repository.getCoursesForSemester(sem.id).first()

                var semSum = 0.0
                if (courses.isNotEmpty()) {
                    courses.forEach { course ->
                        val avg = calculateAverageUseCase(course.evaluations.map { it.config }, course.evaluations.flatMap { it.grades })
                        semSum += avg

                        if (avg >= 10.5) totalApproved++ else totalFailed++
                    }
                    val finalSemAvg = semSum / courses.size
                    semesterAveragesList.add(finalSemAvg)
                    sumSemesterAverages += finalSemAvg

                    if (finalSemAvg > bestAvg) {
                        bestAvg = finalSemAvg
                        bestName = sem.name
                    }
                } else {
                    semesterAveragesList.add(0.0)
                }
            }

            // 3. Totales
            val careerAvg = if (semesters.isNotEmpty()) sumSemesterAverages / semesters.size else 0.0

            _uiState.value = _uiState.value.copy(
                totalSemesters = semesters.size,
                totalApprovedCourses = totalApproved,
                totalFailedCourses = totalFailed,
                careerAverage = careerAvg,
                bestSemesterName = bestName,
                historyPoints = semesterAveragesList
            )
        }
    }
}