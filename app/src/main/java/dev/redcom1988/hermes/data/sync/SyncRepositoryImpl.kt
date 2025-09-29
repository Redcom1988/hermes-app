package dev.redcom1988.hermes.data.sync

import android.util.Log
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.HermesDatabase
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeTaskCrossRefEntity
import dev.redcom1988.hermes.data.local.account_data.mapper.toDomain
import dev.redcom1988.hermes.data.local.account_data.mapper.toEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceTaskCrossRefEntity
import dev.redcom1988.hermes.data.local.attendance.toDomain
import dev.redcom1988.hermes.data.local.attendance.toEntity
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.data.local.client.entity.ClientDataEntity
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.client.toDomain
import dev.redcom1988.hermes.data.local.client.toEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingClientCrossRefEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingUserCrossRefEntity
import dev.redcom1988.hermes.data.local.meeting.toDomain
import dev.redcom1988.hermes.data.local.meeting.toEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeDataCrossRefEntity
import dev.redcom1988.hermes.data.local.service.toDomain
import dev.redcom1988.hermes.data.local.service.toEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.local.task.toDomain
import dev.redcom1988.hermes.data.local.task.toEntity
import dev.redcom1988.hermes.data.local.workhour_plan.entity.WorkhourPlanEntity
import dev.redcom1988.hermes.data.local.workhour_plan.toDomain
import dev.redcom1988.hermes.data.local.workhour_plan.toEntity
import dev.redcom1988.hermes.data.remote.api.BulkSyncApi
import dev.redcom1988.hermes.data.remote.model.requests.BulkSyncApiRequest
import dev.redcom1988.hermes.data.remote.model.responses.BulkSyncApiResponseDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.account_data.mapper.toDto
import dev.redcom1988.hermes.domain.attendance.toDto
import dev.redcom1988.hermes.domain.auth.SyncRepository
import dev.redcom1988.hermes.domain.client.toDto
import dev.redcom1988.hermes.domain.meeting.toDto
import dev.redcom1988.hermes.domain.service.toDto
import dev.redcom1988.hermes.domain.task.toDto
import dev.redcom1988.hermes.domain.workhour_plan.toDto
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepositoryImpl(
    private val api: BulkSyncApi,
    private val userPreference: UserPreference,
    private val db: HermesDatabase,
) : SyncRepository {

    private suspend fun fetchDataFromRemote(lastSyncTime: String): BulkSyncApiResponseDto {
        val response = api.getLatestData(lastSyncTime)
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch latest data from remote: ${response.code}")
        }

        val bodyString = response.body.string()
        return Json.decodeFromString<BulkSyncApiResponseDto>(bodyString)
    }

    private suspend fun upsertIndependentEntities(data: BulkSyncApiResponseDto) {
        db.userDao().upsertUsers(data.users?.map { it.toDomain().toEntity() } ?: emptyList())
        db.divisionDao().upsertDivisions(data.divisions?.map { it.toDomain().toEntity() } ?: emptyList())
//        db.accessDao().upsertAccesses(data.accesses?.map { it.toDomain().toEntity() } ?: emptyList())
        db.clientDao().upsertClients(data.clients?.map { it.toDomain().toEntity() } ?: emptyList())
    }

    private suspend fun upsertEntitiesWithForeignKeys(data: BulkSyncApiResponseDto) {
        db.employeeDao().upsertEmployees(data.employees?.map { it.toDomain().toEntity() } ?: emptyList())
//        db.divisionDao().upsertDivisionAccesses(data.divisionAccesses?.map { it.toDomain().toEntity() } ?: emptyList())
        db.clientDao().upsertClientDataList(data.clientData?.map { it.toDomain().toEntity() } ?: emptyList())
        db.serviceDao().upsertServiceTypes(data.serviceTypes?.map { it.toDomain().toEntity() } ?: emptyList())
        db.serviceDao().upsertServiceTypeFields(data.serviceTypeFields?.map { it.toDomain().toEntity() } ?: emptyList())
        db.serviceDao().upsertServices(data.services?.map { it.toDomain().toEntity() } ?: emptyList())
        db.serviceDao().upsertServiceTypeData(data.serviceTypeData?.map { it.toDomain().toEntity() } ?: emptyList())
        db.taskDao().upsertTasks(data.tasks?.map { it.toDomain().toEntity() } ?: emptyList())
        db.meetingDao().upsertMeetings(data.meetings?.map { it.toDomain().toEntity() } ?: emptyList())
        db.attendanceDao().upsertAttendances(data.attendances?.map { it.toDomain().toEntity() } ?: emptyList())
        db.workhourPlanDao().upsertPlans(data.workhourPlans?.map { it.toDomain().toEntity() } ?: emptyList())
    }

    private suspend fun upsertJunctionTables(data: BulkSyncApiResponseDto) {
        db.employeeTaskDao().upsertLinks(data.employeeTasks?.map { it.toDomain().toEntity() } ?: emptyList())
        db.meetingUserDao().upsertLinks(data.meetingUsers?.map { it.toDomain().toEntity() } ?: emptyList())
        db.meetingClientDao().upsertLinks(data.meetingClients?.map { it.toDomain().toEntity() } ?: emptyList())
        db.attendanceTaskDao().upsertLinks(data.attendanceTasks?.map { it.toDomain().toEntity() } ?: emptyList())
    }

    data class PendingChanges(
        val clients: List<ClientEntity>,
        val clientData: List<ClientDataEntity>,
        val services: List<ServiceEntity>,
        val serviceTypeData: List<ServiceTypeDataCrossRefEntity>,
        val tasks: List<TaskEntity>,
        val employeeTasks: List<EmployeeTaskCrossRefEntity>,
        val meetings: List<MeetingEntity>,
        val meetingUsers: List<MeetingUserCrossRefEntity>,
        val meetingClients: List<MeetingClientCrossRefEntity>,
        val attendances: List<AttendanceEntity>,
        val attendanceTasks: List<AttendanceTaskCrossRefEntity>,
        val workhourPlans: List<WorkhourPlanEntity>
    )

    private fun PendingChanges.hasAny(): Boolean {
        return clients.isNotEmpty() ||
                clientData.isNotEmpty() ||
                services.isNotEmpty() ||
                serviceTypeData.isNotEmpty() ||
                tasks.isNotEmpty() ||
                employeeTasks.isNotEmpty() ||
                meetings.isNotEmpty() ||
                meetingUsers.isNotEmpty() ||
                meetingClients.isNotEmpty() ||
                attendances.isNotEmpty() ||
                attendanceTasks.isNotEmpty() ||
                workhourPlans.isNotEmpty()
    }

    private suspend fun collectPendingChanges(): PendingChanges {
        return PendingChanges(
            clients = db.clientDao().getPendingSyncClients(),
            clientData = db.clientDao().getPendingSyncClientData(),
            services = db.serviceDao().getPendingSyncServices(),
            serviceTypeData = db.serviceDao().getPendingSyncServiceTypeData(),
            tasks = db.taskDao().getPendingSyncTasks(),
            employeeTasks = db.employeeTaskDao().getPendingSyncLinks(),
            meetings = db.meetingDao().getPendingSyncMeetings(),
            meetingUsers = db.meetingUserDao().getPendingSyncLinks(),
            meetingClients = db.meetingClientDao().getPendingSyncLinks(),
            attendances = db.attendanceDao().getPendingSyncAttendances(),
            attendanceTasks = db.attendanceTaskDao().getPendingSyncLinks(),
            workhourPlans = db.workhourPlanDao().getPendingSyncWorkhourPlans()
        )
    }

    private suspend fun pushLocalChanges(pending: PendingChanges) {
        val request = BulkSyncApiRequest(
            attendances = pending.attendances.map { it.toDomain().toDto() },
            attendanceTasks = pending.attendanceTasks.map { it.toDomain().toDto() },
            clients = pending.clients.map { it.toDomain().toDto() },
            clientData = pending.clientData.map { it.toDomain().toDto() },
            meetings = pending.meetings.map { it.toDomain().toDto() },
            meetingUsers = pending.meetingUsers.map { it.toDomain().toDto() },
            meetingClients = pending.meetingClients.map { it.toDomain().toDto() },
            services = pending.services.map { it.toDomain().toDto() },
            serviceTypeData = pending.serviceTypeData.map { it.toDomain().toDto() },
            tasks = pending.tasks.map { it.toDomain().toDto() },
            employeeTasks = pending.employeeTasks.map { it.toDomain().toDto() },
            workhourPlans = pending.workhourPlans.map { it.toDomain().toDto() }
        )

        Log.d("API", "Pushing local changes: $request")
        val response = api.pushLocalChanges(request)
        if (!response.isSuccessful) {
            throw Exception("Failed to push local changes to remote: ${response.code}")
        }
    }


    private suspend fun upsertIncomingData(incomingData: BulkSyncApiResponseDto) {
        try {
            upsertIndependentEntities(incomingData)
            upsertEntitiesWithForeignKeys(incomingData)
            upsertJunctionTables(incomingData)
        } catch (e: Exception) {
            throw Exception("Failed to insert sync data into local database", e)
        }
    }

    override suspend fun performSync(lastSyncTime: String, forceClearDataOverride: Boolean) {
        val pendingChanges = collectPendingChanges()
        Log.d("API", "Pending local changes: $pendingChanges")

        if (pendingChanges.hasAny() && !forceClearDataOverride) {
            try {
                pushLocalChanges(pendingChanges)
                Log.d("API", "Successfully pushed local changes to remote")
            } catch (e: Exception) {
                Log.e("API", "Failed to push local changes: ${e.message}")
                throw Exception("Sync failed: Unable to push local changes to server", e)
            }
        }

        clearLocalData()
        val incomingData = fetchDataFromRemote(lastSyncTime) // will throw if 404
        Log.d("API", "Fetched incoming data: $incomingData")

        upsertIncomingData(incomingData)
        userPreference.lastSyncTime().set(formattedNow())
    }

    override suspend fun clearLocalData() {
        Log.d("API", "Clearing local database before upserting incoming data")
        try {
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }
        } catch (e: Exception) {
            throw Exception("Failed to clear local database", e)
        }
    }

}