package dev.redcom1988.hermes.data.local.client.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formattedNow

const val clientDataEntityTableName = "client_data"

@Entity(
    tableName = clientDataEntityTableName,
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ClientDataEntity(
    @PrimaryKey val dataId: Int,
    val clientId: Int,
    val accountType: String,
    val accountCredentials: String,
    val accountPassword: String,
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)