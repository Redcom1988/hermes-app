package dev.redcom1988.hermes.data.local.meeting.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.domain.common.SyncStatus
import java.time.LocalDateTime

const val meetingEntityTableName = "meetings"

@Entity(
    tableName = meetingEntityTableName,
)
data class MeetingEntity(
    @PrimaryKey val meetingId: Int,
    val title: String,
    val note: String? = null,
    val startTime: String,
    val endTime: String,
    val isDeleted: Boolean = false,
    val updatedAt: String = LocalDateTime.now().formatToString(),
    val createdAt: String = LocalDateTime.now().formatToString(),
    val syncStatus: SyncStatus = SyncStatus.CREATED,
)

@Entity(
    tableName = "meeting_clients",
    primaryKeys = ["meetingId", "clientId"],
    foreignKeys = [
        ForeignKey(
            entity = MeetingEntity::class,
            parentColumns = ["meetingId"],
            childColumns = ["meetingId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class MeetingClients(
    val meetingId: Int,
    val clientId: Int
)

@Entity(
    tableName = "meeting_users",
    primaryKeys = ["meetingId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = MeetingEntity::class,
            parentColumns = ["meetingId"],
            childColumns = ["meetingId"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class MeetingUsers (
    val meetingId: Int,
    val userId: Int
)