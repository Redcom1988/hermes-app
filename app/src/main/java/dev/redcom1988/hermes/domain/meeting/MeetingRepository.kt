package dev.redcom1988.hermes.domain.meeting

import kotlinx.coroutines.flow.Flow

interface MeetingRepository {
    fun getMeetingsFlow(): Flow<List<Meeting>>
    fun getUserLinkFlow(): Flow<List<MeetingUserCrossRef>>
    fun getClientLinkFlow(): Flow<List<MeetingClientCrossRef>>
    suspend fun addMeeting(
        title: String,
        note: String?,
        startTime: String,
        endTime: String,
    ): Int
    suspend fun linkUser(meetingId: Int, userId: Int)
    suspend fun linkClient(meetingId: Int, clientId: Int)
    suspend fun unlinkUser(meetingId: Int, userId: Int)
    suspend fun unlinkClient(meetingId: Int, clientId: Int)

    suspend fun update(meeting: Meeting)
    suspend fun deleteMeetingById(meetingId: Int)
    suspend fun getPendingSyncMeetings(): List<Meeting>
    suspend fun getPendingSyncMeetingClients(): List<MeetingClientCrossRef>
    suspend fun getPendingSyncMeetingUsers(): List<MeetingUserCrossRef>
    suspend fun clearLocalMeetingData()
}