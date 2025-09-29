package dev.redcom1988.hermes.data.remote.api.impl

import android.util.Log
import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.WorkhourPlanApi
import dev.redcom1988.hermes.data.remote.model.requests.WorkhourPlanApiRequestDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class WorkhourPlanApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://mock-api.achmad.dev/hermes"
) : WorkhourPlanApi {

    override suspend fun getWorkhourPlans(): Response {
        val request = GET("$baseUrl/workhour-plans")
        val response = networkHelper.client.newCall(request).await()

        Log.d("ASD", "getWorkhourPlans: ${response.code} ${response.message}")

        return response
    }

    override suspend fun pushWorkhourPlanChanges(request: WorkhourPlanApiRequestDto): Response {
        val json = Json.encodeToString(WorkhourPlanApiRequestDto.serializer(), request)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val requestObj = POST("$baseUrl/workhour_plan/sync", body = requestBody)
        return networkHelper.client.newCall(requestObj).await()
    }
}