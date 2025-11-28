package com.example.unsagrades.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: EvaluationConfigEntity)

    // Para guardar los 3 parciales de golpe al crear curso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<EvaluationConfigEntity>)

    @Update
    suspend fun updateConfig(config: EvaluationConfigEntity)

    @Delete
    suspend fun deleteConfig(config: EvaluationConfigEntity)

    // --- LECTURAS ---

    // Obtener configuración de un curso ordenado por número de parcial (1, 2, 3...)
    @Query("SELECT * FROM evaluation_configs WHERE courseId = :courseId ORDER BY partialNumber ASC")
    fun getConfigsForCourse(courseId: String): Flow<List<EvaluationConfigEntity>>

    // Obtener una configuración específica (ej. para editar solo el Parcial 2)
    @Query("SELECT * FROM evaluation_configs WHERE id = :configId LIMIT 1")
    suspend fun getConfigById(configId: String): EvaluationConfigEntity?
}