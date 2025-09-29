package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingUserDto(
    @SerialName("meeting_id")
    val meetingId: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun MeetingUserDto.toDomain() = MeetingUserCrossRef(
    meetingId = meetingId,
    userId = userId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)