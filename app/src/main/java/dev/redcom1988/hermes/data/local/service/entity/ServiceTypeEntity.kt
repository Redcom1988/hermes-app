package dev.redcom1988.hermes.data.local.service.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import java.time.LocalDateTime

@Entity(tableName = "service_types")
data class ServiceTypeEntity(
    @PrimaryKey val serviceTypeId: Int,
    val name: String,
    val description: String? = null,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
)