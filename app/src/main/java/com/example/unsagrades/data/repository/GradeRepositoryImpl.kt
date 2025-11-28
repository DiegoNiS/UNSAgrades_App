package com.example.unsagrades.data.repository

import com.example.unsagrades.data.local.dao.CourseDao
import com.example.unsagrades.data.local.dao.EvaluationConfigDao
import com.example.unsagrades.data.local.dao.GradeDao
import com.example.unsagrades.data.local.dao.SemesterDao
import com.example.unsagrades.data.local.entity.CourseEntity
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.SemesterEntity
import com.example.unsagrades.data.local.relation.CourseWithConfigAndGrades
import com.example.unsagrades.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GradeRepositoryImpl @Inject constructor(
    private val semesterDao: SemesterDao,
    private val courseDao: CourseDao,
    private val configDao: EvaluationConfigDao,
    private val gradeDao: GradeDao
) : GradeRepository {

    // --- SEMESTRES ---
    override fun getAllSemesters(): Flow<List<SemesterEntity>> {
        return semesterDao.getAllSemesters()
    }

    override fun getCurrentSemester(): Flow<SemesterEntity?> {
        return semesterDao.getCurrentSemester()
    }

    // IMPLEMENTACIÓN NUEVA
    override fun getSemesterById(id: String): Flow<SemesterEntity?> {
        return semesterDao.getSemesterById(id)
    }

    override suspend fun getSemesterByName(name: String): SemesterEntity? {
        return semesterDao.getSemesterByName(name)
    }

    override suspend fun saveSemester(semester: SemesterEntity) {
        // Como usamos OnConflictStrategy.REPLACE en el DAO, insert sirve para update también
        semesterDao.insert(semester)
    }

    override suspend fun setAsCurrentSemester(semester: SemesterEntity) {
        semesterDao.setAsCurrentSemester(semester)
    }

    override suspend fun deleteSemester(semester: SemesterEntity) {
        semesterDao.delete(semester)
    }

    // --- CURSOS ---
    override fun getCoursesForSemester(semesterId: String): Flow<List<CourseWithConfigAndGrades>> {
        return courseDao.getCoursesWithConfig(semesterId)
    }

    override fun getCourseDetail(courseId: String): Flow<CourseWithConfigAndGrades?> {
        return courseDao.getCourseDetailById(courseId)
    }

    override suspend fun getCourseById(courseId: String): CourseEntity? {
        return courseDao.getCourseById(courseId)
    }

    // NUEVA IMPLEMENTACIÓN
    override suspend fun getCourseByName(name: String, semesterId: String): CourseEntity? {
        return courseDao.getCourseByName(name, semesterId)
    }

    override suspend fun createCourseWithConfigs(
        course: CourseEntity,
        configs: List<EvaluationConfigEntity>
    ) {
        // Lógica de negocio de datos: Guardar curso y sus pesos asociados
        courseDao.insertCourse(course)
        configDao.insertConfigs(configs)
    }

    override suspend fun deleteCourse(course: CourseEntity) {
        courseDao.deleteCourse(course)
    }

    // --- CONFIGURACIONES ---
    override suspend fun updateConfig(config: EvaluationConfigEntity) {
        configDao.updateConfig(config)
    }

    // --- NOTAS ---
    override suspend fun updateGrade(grade: GradeEntity) {
        gradeDao.insertGrade(grade) // REPLACE strategy maneja insert/update
    }

    override suspend fun deleteGrade(grade: GradeEntity) {
        gradeDao.deleteGrade(grade)
    }
}