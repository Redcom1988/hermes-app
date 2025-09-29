package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.MeetingClientDto
import dev.redcom1988.hermes.data.remote.model.MeetingDto
import dev.redcom1988.hermes.data.remote.model.MeetingUserDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef
import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingApiResponseDto (
    val meetings: List<MeetingDto>?,
    @SerialName("meeting_users")
    val meetingUsers: List<MeetingUserDto>?,
    @SerialName("meeting_clients")
    val meetingClients: List<MeetingClientDto>?,
)

fun MeetingApiResponseDto.toDomainMeetings(): List<Meeting> {
    return meetings?.map { it.toDomain() } ?: emptyList()
}

fun MeetingApiResponseDto.toDomainMeetingUsers(): List<MeetingUserCrossRef> {
    return meetingUsers?.map { it.toDomain() } ?: emptyList()
}

fun MeetingApiResponseDto.toDomainMeetingClients(): List<MeetingClientCrossRef> {
    return meetingClients?.map { it.toDomain() } ?: emptyList()
}