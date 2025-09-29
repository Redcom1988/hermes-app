package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.AccessDto
import dev.redcom1988.hermes.data.remote.model.AttendanceDto
import dev.redcom1988.hermes.data.remote.model.AttendanceTaskDto
import dev.redcom1988.hermes.data.remote.model.ClientDataDto
import dev.redcom1988.hermes.data.remote.model.ClientDto
import dev.redcom1988.hermes.data.remote.model.DivisionAccessDto
import dev.redcom1988.hermes.data.remote.model.DivisionDto
import dev.redcom1988.hermes.data.remote.model.EmployeeDto
import dev.redcom1988.hermes.data.remote.model.EmployeeTaskDto
import dev.redcom1988.hermes.data.remote.model.MeetingClientDto
import dev.redcom1988.hermes.data.remote.model.MeetingDto
import dev.redcom1988.hermes.data.remote.model.MeetingUserDto
import dev.redcom1988.hermes.data.remote.model.ServiceDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeDataDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeFieldDto
import dev.redcom1988.hermes.data.remote.model.TaskDto
import dev.redcom1988.hermes.data.remote.model.UserDto
import dev.redcom1988.hermes.data.remote.model.WorkhourPlanDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.account_data.model.Access
import dev.redcom1988.hermes.domain.account_data.model.Division
import dev.redcom1988.hermes.domain.account_data.model.DivisionAccessCrossRef
import dev.redcom1988.hermes.domain.account_data.model.Employee
import dev.redcom1988.hermes.domain.account_data.model.EmployeeTaskCrossRef
import dev.redcom1988.hermes.domain.account_data.model.User
import dev.redcom1988.hermes.domain.attendance.Attendance
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.ClientData
import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef
import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef
import dev.redcom1988.hermes.domain.service.Service
import dev.redcom1988.hermes.domain.service.ServiceType
import dev.redcom1988.hermes.domain.service.ServiceTypeDataCrossRef
import dev.redcom1988.hermes.domain.service.ServiceTypeField
import dev.redcom1988.hermes.domain.task.AttendanceTaskCrossRef
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BulkSyncApiResponseDto(
    val attendances: List<AttendanceDto>?,
    @SerialName("attendance_tasks")
    val attendanceTasks: List<AttendanceTaskDto>?,
    @SerialName("clients")
    val clients: List<ClientDto>?,
    @SerialName("client_data")
    val clientData: List<ClientDataDto>?,
    @SerialName("meetings")
    val meetings: List<MeetingDto>?,
    @SerialName("meeting_users")
    val meetingUsers: List<MeetingUserDto>?,
    @SerialName("meeting_clients")
    val meetingClients: List<MeetingClientDto>?,
    @SerialName("services")
    val services: List<ServiceDto>?,
    @SerialName("service_types")
    val serviceTypes: List<ServiceTypeDto>?,
    @SerialName("service_type_fields")
    val serviceTypeFields: List<ServiceTypeFieldDto>?,
    @SerialName("service_type_data")
    val serviceTypeData: List<ServiceTypeDataDto>?,
    @SerialName("tasks")
    val tasks: List<TaskDto>?,
    @SerialName("employee_tasks")
    val employeeTasks: List<EmployeeTaskDto>?,
    val users: List<UserDto>?,
    val employees: List<EmployeeDto>?,
    val divisions: List<DivisionDto>?,
//    val accesses: List<AccessDto>?,
//    @SerialName("division_accesses")
//    val divisionAccesses: List<DivisionAccessDto>?,
    @SerialName("workhour_plans")
    val workhourPlans: List<WorkhourPlanDto>?
)

fun BulkSyncApiResponseDto.toDomainAttendances(): List<Attendance> {
    return attendances?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainAttendanceTasks(): List<AttendanceTaskCrossRef> {
    return attendanceTasks?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainClients(): List<Client> {
    return clients?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainClientData(): List<ClientData> {
    return clientData?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainMeetings(): List<Meeting> {
    return meetings?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainMeetingUsers(): List<MeetingUserCrossRef> {
    return meetingUsers?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainMeetingClients(): List<MeetingClientCrossRef> {
    return meetingClients?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainServices(): List<Service> {
    return services?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainServiceTypes(): List<ServiceType> {
    return serviceTypes?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainServiceTypeFields(): List<ServiceTypeField> {
    return serviceTypeFields?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainServiceTypeData(): List<ServiceTypeDataCrossRef> {
    return serviceTypeData?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainTasks(): List<Task> {
    return tasks?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainEmployeeTasks(): List<EmployeeTaskCrossRef> {
    return employeeTasks?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainUsers(): List<User> {
    return users?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainEmployees(): List<Employee> {
    return employees?.map { it.toDomain() } ?: emptyList()
}

fun BulkSyncApiResponseDto.toDomainDivisions(): List<Division> {
    return divisions?.map { it.toDomain() } ?: emptyList()
}

//fun BulkSyncApiResponseDto.toDomainAccesses(): List<Access> {
//    return accesses?.map { it.toDomain() } ?: emptyList()
//}
//
//fun BulkSyncApiResponseDto.toDomainDivisionAccesses(): List<DivisionAccessCrossRef> {
//    return divisionAccesses?.map { it.toDomain() } ?: emptyList()
//}

fun BulkSyncApiResponseDto.toDomainPlans(): List<WorkhourPlan> {
    return workhourPlans?.map { it.toDomain() } ?: emptyList()
}