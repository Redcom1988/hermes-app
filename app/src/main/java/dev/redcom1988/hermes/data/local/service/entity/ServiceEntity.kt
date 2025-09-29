package dev.redcom1988.hermes.data.local.service.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity

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
    val startTime: String = formattedNow(),
    val expireTime: String = formattedNow(),
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)





