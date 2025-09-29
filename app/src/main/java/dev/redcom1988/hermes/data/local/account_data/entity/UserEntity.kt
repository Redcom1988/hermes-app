package dev.redcom1988.hermes.data.local.account_data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.domain.account_data.enums.UserRole

const val userEntityTableName = "users"

@Entity(tableName = userEntityTableName)
data class UserEntity(
    @PrimaryKey val userId: Int,
    val email: String,
    val role: UserRole,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String,
)