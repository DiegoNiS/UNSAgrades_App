package com.example.unsagrades.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.unsagrades.data.local.dao.CourseDao
import com.example.unsagrades.data.local.dao.EvaluationConfigDao
import com.example.unsagrades.data.local.dao.GradeDao
import com.example.unsagrades.data.local.dao.SemesterDao
import com.example.unsagrades.data.local.entity.CourseEntity
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.GradeType
import com.example.unsagrades.data.local.entity.SemesterEntity

@Database(
    entities = [
        SemesterEntity::class,
        CourseEntity::class,
        EvaluationConfigEntity::class,
        GradeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun semesterDao(): SemesterDao
    abstract fun courseDao(): CourseDao
    abstract fun evaluationConfigDao(): EvaluationConfigDao
    abstract fun gradeDao(): GradeDao
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