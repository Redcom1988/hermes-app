package dev.redcom1988.hermes.data.remote.model.requests

import dev.redcom1988.hermes.data.remote.model.WorkhourPlanDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkhourPlanApiRequestDto(
    @SerialName("workhour_plans")
    val workhourPlans: List<WorkhourPlanDto>? = emptyList()
)