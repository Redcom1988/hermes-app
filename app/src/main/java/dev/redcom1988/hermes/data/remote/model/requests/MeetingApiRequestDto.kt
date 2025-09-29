package dev.redcom1988.hermes.data.remote.model.requests

import dev.redcom1988.hermes.data.remote.model.MeetingClientDto
import dev.redcom1988.hermes.data.remote.model.MeetingDto
import dev.redcom1988.hermes.data.remote.model.MeetingUserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MeetingApiRequestDto(
    val meetings: List<MeetingDto>? = emptyList(),
    @SerialName("meeting_users")
    val meetingUsers: List<MeetingUserDto>? = emptyList(),
    @SerialName("meeting_clients")
    val meetingClients: List<MeetingClientDto>? = emptyList(),
)