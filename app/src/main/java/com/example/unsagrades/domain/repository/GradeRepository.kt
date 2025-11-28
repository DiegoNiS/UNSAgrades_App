package com.example.unsagrades.domain.repository

import com.example.unsagrades.data.local.entity.CourseEntity
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.SemesterEntity
import com.example.unsagrades.data.local.relation.CourseWithConfigAndGrades
import kotlinx.coroutines.flow.Flow

interface GradeRepository {

    // --- SEMESTRES ---
    fun getAllSemesters(): Flow<List<SemesterEntity>>

    fun getCurrentSemester(): Flow<SemesterEntity?>

    // NUEVO: Obtener un semestre específico por ID (para el historial)
    fun getSemesterById(id: String): Flow<SemesterEntity?>

    // NUEVO
    suspend fun getSemesterByName(name: String): SemesterEntity?

    suspend fun saveSemester(semester: SemesterEntity) // Maneja Insert y Update

    suspend fun setAsCurrentSemester(semester: SemesterEntity)

    suspend fun deleteSemester(semester: SemesterEntity)

    // --- CURSOS ---
    fun getCoursesForSemester(semesterId: String): Flow<List<CourseWithConfigAndGrades>>

    fun getCourseDetail(courseId: String): Flow<CourseWithConfigAndGrades?>

    suspend fun getCourseById(courseId: String): CourseEntity?

    // NUEVO
    suspend fun getCourseByName(name: String, semesterId: String): CourseEntity?

    /**
     * Guarda un curso completo: La entidad Curso y sus configuraciones de parciales (pesos).
     * Se usa al crear una asignatura nueva.
     */
    suspend fun createCourseWithConfigs(course: CourseEntity, configs: List<EvaluationConfigEntity>)

    suspend fun deleteCourse(course: CourseEntity)

    // --- CONFIGURACIONES (PESOS) ---
    suspend fun updateConfig(config: EvaluationConfigEntity)

    // --- NOTAS ---
    suspend fun updateGrade(grade: GradeEntity) // Sirve para Insertar o Actualizar si usamos REPLACE

    suspend fun deleteGrade(grade: GradeEntity) // Para eliminar el Sustitutorio
}