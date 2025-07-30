package dev.redcom1988.hermes.data.local.meeting

import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.data.remote.api.MeetingApi
import dev.redcom1988.hermes.data.remote.model.MeetingDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.common.SyncStatus
import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingRepository
import dev.redcom1988.hermes.domain.meeting.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class MeetingRepositoryImpl (
    private val meetingDao: MeetingDao,
    private val api: MeetingApi
) : MeetingRepository {

    override fun getMeetingsFlow(): Flow<List<Meeting>> {
        return meetingDao.getVisibleMeetingsFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    suspend fun syncMeetingsFromServer(serverMeetings: List<Meeting>) {
        val localMeetings = meetingDao.getAllMeetings()
            .associateBy { it.meetingId }

        val mergedMeetings = serverMeetings.map { server ->
            val serverEntity = server.toEntity()
            val local = localMeetings[server.id]

            when {
                local == null -> serverEntity
                local.syncStatus != SyncStatus.UNCHANGED && local.updatedAt > serverEntity.updatedAt -> local
                else -> serverEntity.copy(syncStatus = SyncStatus.UNCHANGED)
            }
        }
        meetingDao.insertMeetings(mergedMeetings)
    }

    suspend fun generateTempMeetingId(): Int {
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
            syncStatus = SyncStatus.CREATED
        )
        meetingDao.insertMeeting(entity)
        return tempId
    }

    override suspend fun updateMeeting(meeting: Meeting) {
        val entity = meeting.toEntity().copy(
            syncStatus = SyncStatus.UPDATED,
            updatedAt = LocalDateTime.now().formatToString()
        )
        meetingDao.updateMeeting(entity)
    }

    override suspend fun deleteMeetingById(meetingId: Int) {
        meetingDao.softDeleteMeetingById(meetingId, SyncStatus.DELETED)
    }

    override suspend fun getPendingSyncMeetings(): List<MeetingEntity> {
        return meetingDao.getPendingSyncMeetings()
    }

    suspend fun pushPendingMeetingsToServer() {
        val pendingMeetings = getPendingSyncMeetings()

        for (meeting in pendingMeetings) {
            val dto = meeting.toDomain().toDto()

            when (meeting.syncStatus) {
                SyncStatus.CREATED -> {
                    val result = api.createMeeting(dto)
                    if (result.isSuccessful) {
                        val serverMeeting = result.parseAs<MeetingDto>().toDomain()
                        meetingDao.insertMeeting(serverMeeting.toEntity()
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                SyncStatus.UPDATED -> {
                    val result = api.updateMeeting(meeting.meetingId, dto)
                    if (result.isSuccessful) {
                        meetingDao.updateMeeting(meeting
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                SyncStatus.DELETED -> {
                    val deletedDto = dto.copy(isDeleted = true)
                    val result = api.updateMeeting(meeting.meetingId, deletedDto)
                    if (result.isSuccessful) {
                        meetingDao.updateMeeting(meeting
                            .copy(syncStatus = SyncStatus.UNCHANGED))
                    }
                }

                else -> Unit
            }
        }
    }

    override suspend fun syncMeetings() {
        pushPendingMeetingsToServer()

        val response = api.getMeetings()
        if (response.isSuccessful) {
            val meetingsFromServer = response
                .parseAs<List<MeetingDto>>()
                .map { dto: MeetingDto -> dto.toDomain() }
            syncMeetingsFromServer(serverMeetings = meetingsFromServer)
        }
    }

    override suspend fun clearLocalMeetings() {
        meetingDao.deleteAllMeetings()
    }
}