package dev.redcom1988.hermes.data.local.meeting.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity


@Entity(
    tableName = "meeting_clients",
    primaryKeys = ["meetingId", "clientId"],
    indices = [
        Index(value = ["meetingId"]),
        Index(value = ["clientId"]),
    ],
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
    ]
)
data class MeetingClientCrossRefEntity(
    val meetingId: Int,
    val clientId: Int,
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)