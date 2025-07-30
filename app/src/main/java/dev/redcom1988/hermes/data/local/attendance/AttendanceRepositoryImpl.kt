package dev.redcom1988.hermes.data.local.attendance

import dev.redcom1988.hermes.core.util.extension.formatToString
import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceWithTasks
import dev.redcom1988.hermes.data.remote.api.AttendanceApi
import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.attendance.AttendanceWorkLocation
import dev.redcom1988.hermes.domain.attendance.toDto
import dev.redcom1988.hermes.domain.common.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class AttendanceRepositoryImpl(
    private val attendanceDao: AttendanceDao,
    private val api: AttendanceApi
) : AttendanceRepository {

    override fun getAttendancesFlow(): Flow<List<Attendance>> {
        return attendanceDao.getVisibleAttendancesFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun getAttendancesWithTasksFlow(): Flow<List<AttendanceWithTasks>> {
        return attendanceDao.getVisibleAttendancesWithTasksFlow()
            .map { list -> list.map { it.copy(tasks = it.tasks) } }
    }

    suspend fun syncAttendancesFromServer(serverAttendances: List<Attendance>) {
        val localAttendances = attendanceDao.getAllAttendances()
            .associateBy { it.attendanceId }

        val mergedAttendances = serverAttendances.map { server ->
            val serverEntity = server.toEntity()
            val local = localAttendances[server.id]

            when {
                // No local version → insert
                local == null -> serverEntity

                // Local was modified more recently → keep local
                local.syncStatus != SyncStatus.UNCHANGED && local.updatedAt > serverEntity.updatedAt -> local

                // Server is newer → overwrite
                else -> serverEntity.copy(syncStatus = SyncStatus.UNCHANGED)
            }
        }
        attendanceDao.insertAttendances(mergedAttendances)
    }

    override suspend fun deleteAttendanceByUserId(userId: Int) {
        attendanceDao.softDeleteAttendanceById(userId, SyncStatus.DELETED)
    }

    suspend fun generateTempAttendanceId(): Int {
        val minId = attendanceDao.getMinTempAttendanceId() ?: 0
        return if (minId >= 0) -1 else minId - 1
    }

    override suspend fun clockInUser(
        userId: Int,
        workLocation: AttendanceWorkLocation,
        imagePath: String?,
        longitude: Double,
        latitude: Double,
    ): Int {
        val tempId = generateTempAttendanceId()
        val attendance = AttendanceEntity(
            attendanceId = tempId,
            userId = userId,
            workLocation = workLocation,
            imagePath = imagePath,
            longitude = longitude,
            latitude = latitude
        )
        attendanceDao.insertAttendance(attendance)
        return tempId
    }

    override suspend fun updateAttendance(attendance: Attendance) {
        val entity = attendance.toEntity().copy(
            syncStatus = SyncStatus.UPDATED,
            updatedAt = LocalDateTime.now().formatToString())
        attendanceDao.updateAttendance(entity)
    }

    override suspend fun clockOutUser(
        userId: Int,
        taskLink: String?
    ) {
        val currentAttendance = attendanceDao.getActiveAttendanceForUser(userId)
        if (currentAttendance != null) {
            val updated = currentAttendance.copy(
                endTime = LocalDateTime.now().formatToString(),
                taskLink = taskLink,
                syncStatus = SyncStatus.UPDATED,
                updatedAt = LocalDateTime.now().formatToString()
            )
            attendanceDao.updateAttendance(updated)
        }
    }

    override suspend fun getPendingSyncAttendances(): List<AttendanceEntity> {
        return attendanceDao.getPendingSyncAttendances()
    }

    suspend fun pushPendingAttendancesToServer() {
        val pendingAttendances = getPendingSyncAttendances()

        for (attendance in pendingAttendances) {
            val dto = attendance.toDomain().toDto()

            when (attendance.syncStatus) {
                SyncStatus.CREATED -> {
                    val result = api.createAttendance(dto)
                    if (result.isSuccessful) {
                        val serverAttendance = result.parseAs<AttendanceDto>().toDomain()
                        attendanceDao.insertAttendance(
                            serverAttendance.toEntity()
                                .copy(syncStatus = SyncStatus.UNCHANGED)
                        )
                    }
                }

                SyncStatus.UPDATED -> {
                    val result = api.updateAttendance(attendance.attendanceId, dto)
                    if (result.isSuccessful) {
                        attendanceDao.updateAttendance(
                            attendance
                                .copy(syncStatus = SyncStatus.UNCHANGED)
                        )
                    }
                }

                SyncStatus.DELETED -> {
                    val deletedDto = dto.copy(isDeleted = true)
                    val result = api.updateAttendance(attendance.attendanceId, deletedDto)
                    if (result.isSuccessful) {
                        attendanceDao.updateAttendance(
                            attendance
                                .copy(syncStatus = SyncStatus.UNCHANGED)
                        )
                    }
                }

                else -> Unit
            }
        }
    }

    override suspend fun syncAttendances() {
        pushPendingAttendancesToServer()

        val response = api.getAttendances()
        if (response.isSuccessful) {
            val serverAttendances = response
                .parseAs<List<AttendanceDto>>()
                .map { it.toDomain() }
            syncAttendancesFromServer(serverAttendances = serverAttendances)
        }
    }

    override suspend fun clearLocalAttendances() {
        attendanceDao.deleteAllAttendances()
    }
}
