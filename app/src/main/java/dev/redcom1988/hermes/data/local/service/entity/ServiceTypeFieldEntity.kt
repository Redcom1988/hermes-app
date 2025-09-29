package dev.redcom1988.hermes.data.local.service.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import java.time.LocalDateTime

@Entity(
    tableName = "service_type_fields",
    foreignKeys = [
        ForeignKey(
            entity = ServiceTypeEntity::class,
            parentColumns = ["serviceTypeId"],
            childColumns = ["serviceTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ServiceTypeFieldEntity(
    @PrimaryKey val fieldId: Int,
    val serviceTypeId: Int,
    val fieldName: String,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString()
)