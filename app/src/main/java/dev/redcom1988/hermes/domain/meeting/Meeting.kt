package dev.redcom1988.hermes.domain.meeting

data class Meeting(
    val id: Int,
    val title: String,
    val note: String? = null,
    val startTime: String,
    val endTime: String,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String,
)