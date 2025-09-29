package dev.redcom1988.hermes.domain.meeting

import dev.redcom1988.hermes.data.remote.model.MeetingClientDto
import dev.redcom1988.hermes.data.remote.model.MeetingDto
import dev.redcom1988.hermes.data.remote.model.MeetingUserDto

fun Meeting.toDto(): MeetingDto {
    return MeetingDto(
        id = id,
        title = title,
        note = note,
        startTime = startTime,
        endTime = endTime,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

fun MeetingUserCrossRef.toDto(): MeetingUserDto {
    return MeetingUserDto(
        userId = userId,
        meetingId = meetingId,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

fun MeetingClientCrossRef.toDto(): MeetingClientDto {
    return MeetingClientDto(
        clientId = clientId,
        meetingId = meetingId,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}