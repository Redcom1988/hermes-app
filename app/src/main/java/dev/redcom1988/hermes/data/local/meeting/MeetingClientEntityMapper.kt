package dev.redcom1988.hermes.data.local.meeting

import dev.redcom1988.hermes.data.local.meeting.entity.MeetingClientCrossRefEntity
import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef

fun MeetingClientCrossRefEntity.toDomain() = MeetingClientCrossRef(
    meetingId = meetingId,
    clientId = clientId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun MeetingClientCrossRef.toEntity(isSynced: Boolean = true) = MeetingClientCrossRefEntity(
    meetingId = meetingId,
    clientId = clientId,
    isSynced = isSynced,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)