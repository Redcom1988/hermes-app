package dev.redcom1988.hermes.data.local.meeting.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import dev.redcom1988.hermes.core.util.extension.formattedNow


@Entity(
    tableName = "meeting_users",
    primaryKeys = ["meetingId", "userId"],
    indices = [
        Index(value = ["meetingId"]),
        Index(value = ["userId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = MeetingEntity::class,
            parentColumns = ["meetingId"],
            childColumns = ["meetingId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class MeetingUserCrossRefEntity(
    val meetingId: Int,
    val userId: Int,
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)