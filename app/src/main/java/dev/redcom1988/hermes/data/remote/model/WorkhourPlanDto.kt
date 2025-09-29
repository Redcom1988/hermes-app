package dev.redcom1988.hermes.data.remote.model

import android.util.Log
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkhourPlanDto(
    val id: Int,
    @SerialName("employee_id")
    val employeeId: Int,
    @SerialName("plan_date")
    val planDate: String,
    @SerialName("planned_start_time")
    val plannedStartTime: String,
    @SerialName("planned_end_time")
    val plannedEndTime: String,
    @SerialName("work_location")
    val workLocation: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun WorkhourPlanDto.toDomain() = WorkhourPlan(
    id = id,
    employeeId = employeeId,
    planDate = planDate,
    plannedStartTime = plannedStartTime,
    plannedEndTime = plannedEndTime,
    workLocation = WorkLocation.fromNameOrLabel(workLocation),
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

