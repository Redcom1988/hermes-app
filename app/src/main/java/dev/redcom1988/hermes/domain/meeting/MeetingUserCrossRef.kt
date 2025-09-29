package dev.redcom1988.hermes.domain.meeting

class MeetingUserCrossRef(
    val meetingId: Int,
    val userId: Int,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String
)