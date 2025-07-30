package dev.redcom1988.hermes.domain.meeting

import dev.redcom1988.hermes.data.remote.model.MeetingDto

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