package com.example.unsagrades.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.unsagrades.data.local.entity.CourseEntity
import com.example.unsagrades.data.local.relation.CourseWithConfigAndGrades
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    // Esta es la consulta "Mágica". Trae el curso, sus parciales y sus notas en un solo objeto.
    @Transaction
    @Query("SELECT * FROM courses WHERE semesterId = :semesterId")
    fun getCoursesWithConfig(semesterId: String): Flow<List<CourseWithConfigAndGrades>>

    @Query("SELECT * FROM courses WHERE id = :courseId LIMIT 1")
    suspend fun getCourseById(courseId: String): CourseEntity?

    // NUEVO: Buscar curso por nombre dentro de un semestre específico
    @Query("SELECT * FROM courses WHERE name = :name AND semesterId = :semesterId LIMIT 1")
    suspend fun getCourseByName(name: String, semesterId: String): CourseEntity?

    // Nueva consulta para obtener el detalle completo de un solo curso
    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId LIMIT 1")
    fun getCourseDetailById(courseId: String): Flow<CourseWithConfigAndGrades?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)
}