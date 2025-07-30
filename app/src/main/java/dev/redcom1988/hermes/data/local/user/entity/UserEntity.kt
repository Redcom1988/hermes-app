package dev.redcom1988.hermes.data.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val userEntityTableName = "users"

@Entity(tableName = userEntityTableName)
data class UserEntity(
    @PrimaryKey val userId: Int,
    val email: String,
    val role: String,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String,
)