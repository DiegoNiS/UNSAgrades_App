package com.example.unsagrades.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.unsagrades.data.local.dao.CourseDao
import com.example.unsagrades.data.local.dao.EvaluationConfigDao
import com.example.unsagrades.data.local.dao.GradeDao
import com.example.unsagrades.data.local.dao.SemesterDao
import com.example.unsagrades.data.local.dao.UserDao
import com.example.unsagrades.data.local.entity.CourseEntity
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.GradeType
import com.example.unsagrades.data.local.entity.SemesterEntity
import com.example.unsagrades.data.local.entity.UserEntity

@Database(
    entities = [
        SemesterEntity::class,
        CourseEntity::class,
        EvaluationConfigEntity::class,
        GradeEntity::class,
        UserEntity::class // <--- NUEVA ENTIDAD AGREGADA
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun semesterDao(): SemesterDao
    abstract fun courseDao(): CourseDao
    abstract fun evaluationConfigDao(): EvaluationConfigDao
    abstract fun gradeDao(): GradeDao
    abstract fun userDao(): UserDao // <--- NUEVO DAO

    companion object {
        // ESTRATEGIA DE MIGRACIÓN MANUAL:
        // Le decimos a Room exactamente qué comando SQL ejecutar para actualizar la estructura
        // sin tocar los datos existentes.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Creamos la tabla nueva. Copiamos la definición exacta de UserEntity.
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_profile` (
                        `id` INTEGER NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `avatarId` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}

// --- CONVERTERS ---
// Ayuda a Room a entender tipos complejos como Enums
class Converters {
    @TypeConverter
    fun fromGradeType(value: GradeType): String {
        return value.name
    }

    @TypeConverter
    fun toGradeType(value: String): GradeType {
        return try {
            GradeType.valueOf(value)
        } catch (e: Exception) {
            GradeType.EXAM // Fallback por seguridad
        }
    }
}