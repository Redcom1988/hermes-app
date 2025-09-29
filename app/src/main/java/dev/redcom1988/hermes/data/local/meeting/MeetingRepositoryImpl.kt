package dev.redcom1988.hermes.data.local.meeting

import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingClientCrossRefEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingUserCrossRefEntity
import dev.redcom1988.hermes.data.remote.api.MeetingApi
import dev.redcom1988.hermes.data.remote.model.requests.MeetingApiRequestDto
import dev.redcom1988.hermes.data.remote.model.responses.MeetingApiResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.toDomainMeetingClients
import dev.redcom1988.hermes.data.remote.model.responses.toDomainMeetingUsers
import dev.redcom1988.hermes.data.remote.model.responses.toDomainMeetings
import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef
import dev.redcom1988.hermes.domain.meeting.MeetingRepository
import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef
import dev.redcom1988.hermes.domain.meeting.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class MeetingRepositoryImpl(
    private val meetingDao: MeetingDao,
    private val meetingClientDao: MeetingClientDao,
    private val meetingUserDao: MeetingUserDao,
    private val api: MeetingApi
) : MeetingRepository {

    override fun getMeetingsFlow(): Flow<List<Meeting>> {
        return meetingDao.getVisibleMeetingsFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getUserLinkFlow(): Flow<List<MeetingUserCrossRef>> {
        return meetingUserDao.getVisibleLinks()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getClientLinkFlow(): Flow<List<MeetingClientCrossRef>> {
        return meetingClientDao.getVisibleLinks()
            .map { list -> list.map { it.toDomain() } }
    }

    private suspend fun generateTempMeetingId(): Int {
        val minTempId = meetingDao.getMinTempMeetingId() ?: 0
        return minTempId - 1
    }

    override suspend fun addMeeting(
        title: String,
        note: String?,
        startTime: String,
        endTime: String
    ): Int {
        val tempId = generateTempMeetingId()
        val entity = MeetingEntity(
            meetingId = tempId,
            title = title,
            note = note,
            startTime = startTime,
            endTime = endTime,
            isDeleted = false,
            isSynced = false,
            updatedAt = formattedNow()
        )
        meetingDao.insertMeeting(entity)
        return tempId
    }

    override suspend fun linkClient(
        meetingId: Int,
        clientId: Int
    ) {
        val entity = MeetingClientCrossRefEntity(
            meetingId = meetingId,
            clientId = clientId,
            isSynced = false
        )
        meetingClientDao.insertLink(entity)
    }

    override suspend fun linkUser(
        meetingId: Int,
        userId: Int
    ) {
        val entity = MeetingUserCrossRefEntity(
            meetingId = meetingId,
            userId = userId,
            isSynced = false
        )
        meetingUserDao.insertLink(entity)
    }

    override suspend fun unlinkClient(meetingId: Int, clientId: Int) {
        meetingClientDao.softDeleteLink(meetingId, clientId, formattedNow())
    }

    override suspend fun unlinkUser(meetingId: Int, userId: Int) {
        meetingUserDao.softDeleteLink(meetingId, userId, formattedNow())
    }

    override suspend fun update(meeting: Meeting) {
        val updated = meeting.copy(updatedAt = formattedNow())
        meetingDao.updateMeeting(updated.toEntity(isSynced = false)
        )
    }


    override suspend fun deleteMeetingById(meetingId: Int) {
        meetingDao.softDeleteMeetingById(meetingId)
    }

    override suspend fun getPendingSyncMeetings(): List<Meeting> {
        return meetingDao.getPendingSyncMeetings().map { it.toDomain() }
    }

    override suspend fun getPendingSyncMeetingClients(): List<MeetingClientCrossRef> {
        return meetingClientDao.getPendingSyncLinks().map { it.toDomain() }
    }

    override suspend fun getPendingSyncMeetingUsers(): List<MeetingUserCrossRef> {
        return meetingUserDao.getPendingSyncLinks().map { it.toDomain() }
    }

    private suspend fun fetchDataFromRemote(): MeetingApiResponseDto {
        val response = api.getMeetingData()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch meeting data from remote: ${response.code}")
        }

        val bodyString = response.body.string()
        return Json.decodeFromString<MeetingApiResponseDto>(bodyString)
    }

    @Transaction
    override suspend fun clearLocalMeetingData() {
        meetingDao.deleteAllMeetings()
        meetingClientDao.deleteAllLinks()
        meetingUserDao.deleteAllLinks()
    }
}
