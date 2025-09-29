package dev.redcom1988.hermes.data.local.workhour_plan

import android.util.Log
import dev.redcom1988.hermes.data.local.workhour_plan.entity.WorkhourPlanEntity
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan

fun WorkhourPlanEntity.toDomain() = WorkhourPlan(
    id = planId,
    employeeId = employeeId,
    planDate = planDate,
    plannedStartTime = plannedStartTime,
    plannedEndTime = plannedEndTime,
    workLocation = workLocation.also {
        Log.d("ASD", "Mapped ENTITY work_location='${workLocation.name}' → ${it.name}, ${it.label}")
    },
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
    workLocation = workLocation.also {
        Log.d("ASD", "Mapped DOMAIN work_location='${workLocation.name}' → ${it.name}, ${it.label}")
    },
    isSynced = isSynced,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)