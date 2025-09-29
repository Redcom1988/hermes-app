package dev.redcom1988.hermes.domain.meeting

class MeetingClientCrossRef(
    val meetingId: Int,
    val clientId: Int,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String
)