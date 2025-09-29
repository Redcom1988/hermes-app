package dev.redcom1988.hermes.data.local.meeting.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.redcom1988.hermes.core.util.extension.formattedNow

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
    val isSynced: Boolean = true,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow()
)

