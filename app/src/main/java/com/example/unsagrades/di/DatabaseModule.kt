package com.example.unsagrades.di

import android.content.Context
import androidx.room.Room
import com.example.unsagrades.data.local.AppDatabase
import com.example.unsagrades.data.local.dao.CourseDao
import com.example.unsagrades.data.local.dao.EvaluationConfigDao
import com.example.unsagrades.data.local.dao.GradeDao
import com.example.unsagrades.data.local.dao.SemesterDao
import com.example.unsagrades.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unsa_tracker_db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2) // <--- AGREGAMOS LA MIGRACIÓN SEGURA
            // .fallbackToDestructiveMigration() <--- ¡ELIMINADO! PARA NO BORRAR DATOS
            //.fallbackToDestructiveMigration() // Útil en desarrollo: si cambias la DB, borra todo y empieza de cero para no crashear
            .build()
    }

    @Provides
    fun provideSemesterDao(db: AppDatabase): SemesterDao = db.semesterDao()

    @Provides
    fun provideCourseDao(db: AppDatabase): CourseDao = db.courseDao()

    @Provides
    fun provideEvaluationConfigDao(db: AppDatabase): EvaluationConfigDao = db.evaluationConfigDao()

    @Provides
    fun provideGradeDao(db: AppDatabase): GradeDao = db.gradeDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao() // Nuevo
}