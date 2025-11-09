package dev.redcom1988.hermes.data.local.attendance

import androidx.room.Transaction
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.task.TaskDao
import dev.redcom1988.hermes.data.local.task.toDomain
import dev.redcom1988.hermes.data.remote.api.AttendanceApi
import dev.redcom1988.hermes.data.remote.model.requests.AttendanceApiRequestDto
import dev.redcom1988.hermes.data.remote.model.responses.AttendanceApiResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.toDomainAttendanceTasks
import dev.redcom1988.hermes.data.remote.model.responses.toDomainAttendances
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import dev.redcom1988.hermes.domain.attendance.AttendanceWithTask
import dev.redcom1988.hermes.domain.attendance.toDto
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.time.Duration
import java.time.Duration as JavaDuration
import kotlin.time.toKotlinDuration

class AttendanceRepositoryImpl(
    private val attendanceDao: AttendanceDao,
    private val attendanceTaskDao: AttendanceTaskDao,
    private val taskDao: TaskDao,
    private val api: AttendanceApi
) : AttendanceRepository {

    // """ LOCAL OPERATIONS """

    // Get the minimum temporary attendance ID for new attendances
    suspend fun getMinTempAttendanceId(): Int {
        val minTempId = attendanceDao.getMinTempAttendanceId() ?: 0
        return minTempId - 1
    }

    // Add new incomplete attendance
    @Transaction
    override suspend fun addAttendance(
        employeeId: Int,
        startTime: String,
        longitude: Double,
        latitude: Double,
        imagePath: String?,
        workLocation: WorkLocation
    ): Int {
        val tempId = getMinTempAttendanceId()
        val entity = AttendanceEntity(
            attendanceId = tempId,
            employeeId = employeeId,
            startTime = startTime,
            endTime = null,
            workLocation = workLocation,
            imagePath = imagePath,
            longitude = longitude,
            latitude = latitude,
            isSynced = false,
            isDeleted = false,
            createdAt = formattedNow(),
            updatedAt = formattedNow()
        )
        attendanceDao.upsertAttendance(entity)
        return tempId
    }

    @Transaction
    override suspend fun finishAttendance(
        employeeId: Int,
        endTime: String,
        taskIds: List<Int>?,
    ) {
        val attendance = attendanceDao.getActiveAttendanceForEmployee(employeeId)

        if (attendance != null) {
            val updated = attendance.copy(
                endTime = endTime,
                isSynced = false,
                isDeleted = false,
                updatedAt = formattedNow()
            )
            attendanceDao.updateAttendance(updated)

            taskIds?.forEach { taskId ->
                attendanceTaskDao.upsertLink(updated.attendanceId, taskId)
                taskDao.markTaskAsCompleted(taskId, TaskStatus.COMPLETED, formattedNow())
            }
        }
    }

    private fun tickerFlow(periodMillis: Long = 1_000L): Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(periodMillis)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Transaction
    override fun observeCurrentAttendanceTime(employeeId: Int): Flow<Duration?> {
        return attendanceDao.observeActiveAttendanceForEmployee(employeeId)
            .flatMapLatest { attendance ->
                if (attendance == null) {
                    flowOf(null)
                } else {
                    tickerFlow() // emit every second
                        .map {
                            val start = attendance.startTime.toLocalDateTime()
                            val now = LocalDateTime.now()
                            JavaDuration.between(start, now).toKotlinDuration()
                        }
                }
            }
    }

    override fun observeActiveAttendanceForEmployee(employeeId: Int): Flow<Attendance?> {
        return attendanceDao.observeActiveAttendanceForEmployee(employeeId)
            .map { it?.toDomain() }
    }

//    override suspend fun getCurrentAttendance(employeeId: Int): Attendance? {
//        return attendanceDao.getActiveAttendanceForEmployee(employeeId)?.toDomain()
//    }

    // Unused insert function, commented out for now
//    override suspend fun insert(attendance: Attendance) {
//        attendanceDao.insertAttendance(attendance.toEntity())
//    }

    // Update attendance locally
    override suspend fun update(attendance: Attendance) {
        val updated = attendance.copy(updatedAt = formattedNow())
        attendanceDao.updateAttendance(updated.toEntity(isSynced = false))
    }

    // Soft delete attendance and its links
    @Transaction
    override suspend fun softDeleteAttendanceWithLinks(id: Int) {
        attendanceDao.softDeleteAttendanceById(id, formattedNow())
        attendanceTaskDao.softDeleteLinksByAttendanceId(id, formattedNow())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAttendanceWithTasks(): Flow<List<AttendanceWithTask>> {
        return attendanceDao.getVisibleAttendancesFlow().flatMapLatest { attendances ->
            if (attendances.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    attendances.map { attendance ->
                        attendanceDao.getTasksForAttendance(attendance.attendanceId).map { tasks ->
                            AttendanceWithTask(
                                attendance = attendance.toDomain(),
                                tasks = tasks.map { it.toDomain() }
                            )
                        }
                    }
                ) { it.toList()}
            }
        }
    }

    // Get non-deleted attendances from local
    override fun getVisibleAttendances(): Flow<List<Attendance>> {
        return attendanceDao.getVisibleAttendancesFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getVisibleAttendanceTask(): Flow<List<AttendanceTaskCrossRef>> {
        return attendanceDao.getVisibleAttendanceTasks()
            .map { list -> list.map { it.toDomain() } }
    }

    // Get tasks linked to a specific attendance
    override fun getTasksForAttendance(attendanceId: Int): Flow<List<Task>> {
        return attendanceDao.getTasksForAttendance(attendanceId)
            .map { list -> list.map { it.toDomain() } }
    }

    // Upsert attendance-task link
    override suspend fun upsertAttendanceTaskLink(attendanceId: Int, taskId: Int) {
        attendanceTaskDao.upsertLink(attendanceId, taskId)
    }

    // Soft delete attendance-task link
    override suspend fun softDeleteAttendanceTaskLink(attendanceId: Int, taskId: Int) {
        attendanceTaskDao.softDeleteLink(attendanceId, taskId, formattedNow())
    }

    // """ SYNC OPERATIONS """

    // Insert attendances from remote db
    override suspend fun insertAttendances(attendances: List<Attendance>) {
        attendanceDao.insertAttendances(attendances.map { it.toEntity() })
    }

    // Insert attendance tasks from remote db
    override suspend fun insertAttendanceTasks(attendanceTasks: List<AttendanceTaskCrossRef>) {
        attendanceTaskDao.insertLinks(attendanceTasks.map { it.toEntity() })
    }

    // Get pending sync attendances
    override suspend fun getPendingSyncAttendances(): List<Attendance> {
        return attendanceDao.getPendingSyncAttendances().map { it.toDomain() }
    }

    // Get pending sync attendance tasks
    override suspend fun getPendingSyncAttendanceTasks(): List<AttendanceTaskCrossRef> {
        return attendanceTaskDao.getPendingSyncLinks().map { it.toDomain() }
    }

    // Delete all attendances on local for complete sync
    override suspend fun deleteAllAttendances() {
        attendanceDao.deleteAllAttendances()
    }

    // Delete all attendance tasks on local for complete sync
    override suspend fun deleteAllAttendanceTasks() {
        attendanceTaskDao.deleteAllLinks()
    }

    // """ API OPERATIONS """
//    private suspend fun fetchDataFromRemote(): AttendanceApiResponseDto {
//        val response = api.getAttendanceData()
//        if (!response.isSuccessful) {
//            throw Exception("Failed to fetch attendances from remote: ${response.code}")
//        }
//
//        val bodyString = response.body.string()
//        return Json.decodeFromString<AttendanceApiResponseDto>(bodyString)
//    }

//    @Transaction
//    suspend fun upsertDataFromRemote(response: AttendanceApiResponseDto) {
//        val remoteAttendances = response.toDomainAttendances()
//        val remoteAttendanceTasks = response.toDomainAttendanceTasks()
//
//        remoteAttendances.forEach { attendance ->
//            val entity = attendance.toEntity()
//            attendanceDao.upsertRemoteAttendanceIfClean(entity)
//        }
//
//        remoteAttendanceTasks.forEach { attendanceTask ->
//            val entity = attendanceTask.toEntity()
//            attendanceTaskDao.upsertRemoteLinkIfClean(entity)
//        }
//
//    }

//    suspend fun pushChangesToRemote() {
//        val requestDto = AttendanceApiRequestDto(
//            attendances = getPendingSyncAttendances().map { it.toDto() },
//            attendanceTasks = getPendingSyncAttendanceTasks().map { it.toDto() }
//        )
//        api.pushAttendanceChanges(requestDto)
//    }
//
//    override suspend fun syncAttendances() {
//        pushChangesToRemote()
//        val response = fetchDataFromRemote()
//        upsertDataFromRemote(response)
//    }
}
