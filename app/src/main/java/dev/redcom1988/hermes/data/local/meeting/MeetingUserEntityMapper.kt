package dev.redcom1988.hermes.data.local.meeting

import dev.redcom1988.hermes.data.local.meeting.entity.MeetingUserCrossRefEntity
import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef

fun MeetingUserCrossRefEntity.toDomain() = MeetingUserCrossRef(
    meetingId = meetingId,
    userId = userId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun MeetingUserCrossRef.toEntity(isSynced: Boolean = true) = MeetingUserCrossRefEntity(
    meetingId = meetingId,
    userId = userId,
    isSynced = isSynced,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)