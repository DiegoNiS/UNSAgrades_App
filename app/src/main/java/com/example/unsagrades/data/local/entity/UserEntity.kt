package com.example.unsagrades.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1, // Siempre será 1, solo hay un usuario
    val name: String,
    val avatarId: Int = 0 // 0: Hombre, 1: Mujer, etc. (Usaremos lógica simple por ahora)
)