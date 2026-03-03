package dev.redcom1988.hermes.data.local.workhour_plan

import dev.redcom1988.hermes.data.local.workhour_plan.entity.WorkhourPlanEntity
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan

fun WorkhourPlanEntity.toDomain() = WorkhourPlan(
    id = planId,
    employeeId = employeeId,
    planDate = planDate,
    plannedStartTime = plannedStartTime,
    plannedEndTime = plannedEndTime,
    workLocation = workLocation,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun WorkhourPlan.toEntity(isSynced: Boolean = true) = WorkhourPlanEntity(
    planId = id,
    employeeId = employeeId,
    planDate = planDate,
    plannedStartTime = plannedStartTime,
    plannedEndTime = plannedEndTime,
    workLocation = workLocation,
    isSynced = isSynced,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)