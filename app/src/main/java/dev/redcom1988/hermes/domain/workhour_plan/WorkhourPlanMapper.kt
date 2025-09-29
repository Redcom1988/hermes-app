package dev.redcom1988.hermes.domain.workhour_plan

import dev.redcom1988.hermes.data.remote.model.WorkhourPlanDto

fun WorkhourPlan.toDto(): WorkhourPlanDto {
    return WorkhourPlanDto(
        id = id,
        employeeId = employeeId,
        planDate = planDate,
        plannedStartTime = plannedStartTime,
        plannedEndTime = plannedEndTime,
        workLocation = workLocation.name,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}