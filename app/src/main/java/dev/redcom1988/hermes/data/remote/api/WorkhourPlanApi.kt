package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.WorkhourPlanDto
import dev.redcom1988.hermes.data.remote.model.requests.WorkhourPlanApiRequestDto
import dev.redcom1988.hermes.data.remote.model.responses.WorkhourPlanApiResponseDto
import okhttp3.Response

interface WorkhourPlanApi {
    suspend fun getWorkhourPlans(): Response
    suspend fun pushWorkhourPlanChanges(request: WorkhourPlanApiRequestDto): Response
}