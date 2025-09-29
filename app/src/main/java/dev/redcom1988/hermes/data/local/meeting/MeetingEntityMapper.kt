package dev.redcom1988.hermes.data.local.meeting

import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.domain.meeting.Meeting

fun MeetingEntity.toDomain() = Meeting(
    id = meetingId,
    title = title,
    note = note,
    startTime = startTime,
    endTime = endTime,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun Meeting.toEntity(isSynced: Boolean = true) = MeetingEntity(
    meetingId = id,
    title = title,
    note = note,
    startTime = startTime,
    endTime = endTime,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
    isSynced = isSynced
)