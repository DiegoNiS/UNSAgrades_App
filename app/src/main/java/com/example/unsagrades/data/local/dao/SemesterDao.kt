package com.example.unsagrades.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.unsagrades.data.local.entity.SemesterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterDao {
    @Query("SELECT * FROM semesters WHERE id = :id")
    fun getSemesterById(id: String): Flow<SemesterEntity?>

    @Query("SELECT * FROM semesters ORDER BY isCurrent DESC, name DESC")
    fun getAllSemesters(): Flow<List<SemesterEntity>>

    @Query("SELECT * FROM semesters WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentSemester(): Flow<SemesterEntity?>

    // NUEVO: Buscar por nombre para validar duplicados
    @Query("SELECT * FROM semesters WHERE name = :name LIMIT 1")
    suspend fun getSemesterByName(name: String): SemesterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(semester: SemesterEntity)

    @Update
    suspend fun update(semester: SemesterEntity)

    @Delete
    suspend fun delete(semester: SemesterEntity)

    // Helper: Desmarca el actual
    @Query("UPDATE semesters SET isCurrent = 0 WHERE isCurrent = 1")
    suspend fun clearCurrentFlag()

    // TRANSACCIÓN: "El Rey ha muerto, viva el Rey"
    // Al marcar un semestre como actual, desmarca automáticamente al anterior.
    @Transaction
    suspend fun setAsCurrentSemester(semester: SemesterEntity) {
        if (semester.isCurrent) {
            clearCurrentFlag()
        }
        insert(semester)
    }
}