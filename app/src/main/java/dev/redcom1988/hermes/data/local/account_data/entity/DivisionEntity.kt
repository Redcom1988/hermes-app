package dev.redcom1988.hermes.data.local.account_data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import java.time.LocalDateTime

const val divisionEntityTableName = "divisions"

@Entity(tableName = divisionEntityTableName)
data class DivisionEntity(
    @PrimaryKey val divisionId: Int,
    val divisionName: String,
    val requiredWorkHours: Int,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
)