package dev.redcom1988.hermes.domain.workhour_plan

import dev.redcom1988.hermes.domain.common.WorkLocation

data class WorkhourPlan(
    val id: Int,
    val employeeId: Int,
    val planDate: String,
    val plannedStartTime: String,
    val plannedEndTime: String,
    val workLocation: WorkLocation,
    val isDeleted: Boolean = false,
    val updatedAt: String,
    val createdAt: String
)