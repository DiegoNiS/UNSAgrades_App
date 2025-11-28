package com.example.unsagrades.di

import com.example.unsagrades.data.repository.GradeRepositoryImpl
import com.example.unsagrades.domain.repository.GradeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGradeRepository(
        gradeRepositoryImpl: GradeRepositoryImpl
    ): GradeRepository
}