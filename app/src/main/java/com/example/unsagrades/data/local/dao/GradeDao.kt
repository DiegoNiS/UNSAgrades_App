package com.example.unsagrades.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.unsagrades.data.local.entity.GradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeEntity)

    @Update
    suspend fun updateGrade(grade: GradeEntity)

    @Delete
    suspend fun deleteGrade(grade: GradeEntity)

    // --- NUEVAS LECTURAS ---

    // Obtener todas las notas de un parcial específico (Configuración)
    // Útil para observar cambios solo en el parcial que tienes desplegado en el acordeón
    @Query("SELECT * FROM grades WHERE configId = :configId")
    fun getGradesForConfig(configId: String): Flow<List<GradeEntity>>

    // Obtener una nota específica (por si necesitas validar algo puntual)
    @Query("SELECT * FROM grades WHERE id = :gradeId LIMIT 1")
    suspend fun getGradeById(gradeId: String): GradeEntity?
}