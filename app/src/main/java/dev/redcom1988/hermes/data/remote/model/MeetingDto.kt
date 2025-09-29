package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef
import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingDto(
    val id: Int,
    val title: String,
    val note: String?,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("end_time")
    val endTime: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String,
)

fun MeetingDto.toDomain(): Meeting {
    return Meeting(
        id = this.id,
        title = this.title,
        note = this.note,
        startTime = this.startTime,
        endTime = this.endTime,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}

@Serializable
data class MeetingData(
    @SerialName("meetings")
    val meeting: List<MeetingDto>?,
    @SerialName("meeting_users")
    val meetingUsers: List<MeetingUserDto>?,
    @SerialName("meeting_clients")
    val meetingClients: List<MeetingClientDto>?,
)

fun MeetingData.toMeetingDomain(): List<Meeting> {
    return meeting?.map { it.toDomain() } ?: emptyList()
}

fun MeetingData.toMeetingClientsDomain(): List<MeetingClientCrossRef> {
    return meetingClients?.map { it.toDomain() } ?: emptyList()
}

fun MeetingData.toMeetingUsersDomain(): List<MeetingUserCrossRef> {
    return meetingUsers?.map { it.toDomain() } ?: emptyList()
}