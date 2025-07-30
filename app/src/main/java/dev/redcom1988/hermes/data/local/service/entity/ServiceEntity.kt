package dev.redcom1988.hermes.data.local.service.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import java.time.LocalDateTime

const val serviceEntityTableName = "services"

@Entity(
    tableName = serviceEntityTableName,
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
        ),
        ForeignKey(
            entity = ServiceTypeEntity::class,
            parentColumns = ["serviceTypeId"],
            childColumns = ["serviceTypeId"],
        )
    ]
)
data class ServiceEntity(
    @PrimaryKey val serviceId: Int,
    val clientId: Int,
    val serviceTypeId: Int,
    val status: String,
    val servicePrice: Int,
    val startTime: String = LocalDateTime.now().formatToString(),
    val expireTime: String = LocalDateTime.now().formatToString(),
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
)

@Entity(tableName = "service_types")
data class ServiceTypeEntity(
    @PrimaryKey val serviceTypeId: Int,
    val name: String,
    val description: String? = null,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
)

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
)

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
data class ServiceTypeDataEntity(
    val fieldId: Int,
    val serviceId: Int,
    val value: String = ""
)