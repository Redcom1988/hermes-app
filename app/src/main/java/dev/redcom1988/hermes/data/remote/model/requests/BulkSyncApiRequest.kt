package dev.redcom1988.hermes.data.remote.model.requests

import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import dev.redcom1988.hermes.data.remote.model.AttendanceTaskDto
import dev.redcom1988.hermes.data.remote.model.ClientDataDto
import dev.redcom1988.hermes.data.remote.model.ClientDto
import dev.redcom1988.hermes.data.remote.model.EmployeeTaskDto
import dev.redcom1988.hermes.data.remote.model.MeetingClientDto
import dev.redcom1988.hermes.data.remote.model.MeetingDto
import dev.redcom1988.hermes.data.remote.model.MeetingUserDto
import dev.redcom1988.hermes.data.remote.model.ServiceDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeDataDto
import dev.redcom1988.hermes.data.remote.model.TaskDto
import dev.redcom1988.hermes.data.remote.model.WorkhourPlanDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BulkSyncApiRequest (
    val attendances: List<AttendanceDto>? = emptyList(),
    @SerialName("attendance_tasks")
    val attendanceTasks: List<AttendanceTaskDto>? = emptyList(),
    val clients: List<ClientDto>? = emptyList(),
    @SerialName("client_data")
    val clientData: List<ClientDataDto>? = emptyList(),
    val meetings: List<MeetingDto>? = emptyList(),
    @SerialName("meeting_users")
    val meetingUsers: List<MeetingUserDto>? = emptyList(),
    @SerialName("meeting_clients")
    val meetingClients: List<MeetingClientDto>? = emptyList(),
    val services: List<ServiceDto> = emptyList(),
    @SerialName("service_type_data")
    val serviceTypeData: List<ServiceTypeDataDto>? = emptyList(),
    val tasks: List<TaskDto>? = emptyList(),
    @SerialName("employee_tasks")
    val employeeTasks: List<EmployeeTaskDto>? = emptyList(),
    @SerialName("workhour_plans")
    val workhourPlans: List<WorkhourPlanDto>? = emptyList()
)