package dev.redcom1988.hermes.data.local.service.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import java.time.LocalDateTime

@Entity(
    tableName = "service_type_data",
    primaryKeys = ["fieldId", "serviceId"],
    foreignKeys = [
        ForeignKey(
            entity = ServiceTypeFieldEntity::class,
            parentColumns = ["fieldId"],
            childColumns = ["fieldId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["serviceId"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ServiceTypeDataCrossRefEntity(
    val fieldId: Int,
    val serviceId: Int,
    val value: String = "",
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString()
)