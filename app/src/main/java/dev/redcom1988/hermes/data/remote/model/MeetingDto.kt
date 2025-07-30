package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.meeting.Meeting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingDto (
    val id: Int,
    val title: String,
    val note: String?,
    val startTime: String,
    val endTime: String,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
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