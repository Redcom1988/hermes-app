package dev.redcom1988.hermes.data.local.client.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.domain.common.SyncStatus
import java.time.LocalDateTime

const val clientEntityTableName = "clients"

@Entity(tableName = clientEntityTableName)
data class ClientEntity(
    @PrimaryKey val clientId: Int,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
    val syncStatus: SyncStatus = SyncStatus.CREATED
)