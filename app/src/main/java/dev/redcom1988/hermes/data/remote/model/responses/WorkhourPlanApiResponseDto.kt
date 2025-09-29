package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.WorkhourPlanDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkhourPlanApiResponseDto(
    @SerialName("workhour_plans")
    val workhourPlans: List<WorkhourPlanDto>?
)

fun WorkhourPlanApiResponseDto.toDomainPlans(): List<WorkhourPlan> {
    return workhourPlans?.map { it.toDomain() } ?: emptyList()
}