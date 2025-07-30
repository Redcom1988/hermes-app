package dev.redcom1988.hermes.domain.attendance

import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceWithTasks
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getAttendancesFlow(): Flow<List<Attendance>>
    fun getAttendancesWithTasksFlow(): Flow<List<AttendanceWithTasks>>
    suspend fun syncAttendances()
    suspend fun getPendingSyncAttendances(): List<AttendanceEntity>
    suspend fun clockInUser(
        userId: Int,
        workLocation: AttendanceWorkLocation,
        imagePath: String?,
        longitude: Double,
        latitude: Double,
    ) : Int
    suspend fun clockOutUser(
        userId: Int,
        taskLink: String?,
    )
    suspend fun updateAttendance(attendance: Attendance)
    suspend fun deleteAttendanceByUserId(userId: Int)
    suspend fun clearLocalAttendances()
}