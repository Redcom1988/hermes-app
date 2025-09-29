package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingClientDto(
    @SerialName("meeting_id")
    val meetingId: Int,
    @SerialName("client_id")
    val clientId: Int,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun MeetingClientDto.toDomain() = MeetingClientCrossRef(
    meetingId = meetingId,
    clientId = clientId,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)