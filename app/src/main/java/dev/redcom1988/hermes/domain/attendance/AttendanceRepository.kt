package dev.redcom1988.hermes.domain.attendance

import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import dev.redcom1988.hermes.domain.task.Task
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface AttendanceRepository {
    suspend fun addAttendance(
        employeeId: Int,
        startTime: String,
        longitude: Double,
        latitude: Double,
        imagePath: String?,
        workLocation: WorkLocation
    ): Int
    suspend fun finishAttendance(
        employeeId: Int,
        endTime: String,
        taskIds: List<Int>?
    )
    fun observeCurrentAttendanceTime(employeeId: Int): Flow<Duration?>
    fun observeActiveAttendanceForEmployee(employeeId: Int): Flow<Attendance?>
    fun getVisibleAttendanceTask(): Flow<List<AttendanceTaskCrossRef>>
    fun getVisibleAttendances(): Flow<List<Attendance>>
    fun getAttendanceWithTasks(): Flow<List<AttendanceWithTask>>
    fun getTasksForAttendance(attendanceId: Int): Flow<List<Task>>

    suspend fun update(attendance: Attendance)
    suspend fun upsertAttendanceTaskLink(attendanceId: Int, taskId: Int)
    suspend fun softDeleteAttendanceWithLinks(id: Int)
    suspend fun softDeleteAttendanceTaskLink(attendanceId: Int, taskId: Int)

    suspend fun insertAttendances(attendances: List<Attendance>)
    suspend fun insertAttendanceTasks(attendanceTasks: List<AttendanceTaskCrossRef>)
    suspend fun getPendingSyncAttendances(): List<Attendance>
    suspend fun getPendingSyncAttendanceTasks(): List<AttendanceTaskCrossRef>
    suspend fun deleteAllAttendances()
    suspend fun deleteAllAttendanceTasks()

//    suspend fun syncAttendances()
}
