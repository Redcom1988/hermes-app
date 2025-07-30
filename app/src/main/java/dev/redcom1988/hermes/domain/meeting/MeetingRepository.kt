package dev.redcom1988.hermes.domain.meeting

import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {
    fun getMeetingsFlow(): Flow<List<Meeting>>
    suspend fun addMeeting(
        title: String,
        note: String?,
        startTime: String,
        endTime: String,
    ): Int
    suspend fun updateMeeting(meeting: Meeting)
    suspend fun deleteMeetingById(meetingId: Int)
    suspend fun syncMeetings()
    suspend fun getPendingSyncMeetings(): List<MeetingEntity>
    suspend fun clearLocalMeetings()
}