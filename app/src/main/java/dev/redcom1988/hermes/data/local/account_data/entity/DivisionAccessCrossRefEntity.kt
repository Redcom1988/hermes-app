package dev.redcom1988.hermes.data.local.account_data.entity

import androidx.room.Entity
import dev.redcom1988.hermes.core.util.extension.formatToString
import java.time.LocalDateTime

@Entity(
    tableName = "division_accesses",
    primaryKeys = ["divisionId", "accessId"],
)
data class DivisionAccessCrossRefEntity(
    val divisionId: Int,
    val accessId: Int,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString()
)