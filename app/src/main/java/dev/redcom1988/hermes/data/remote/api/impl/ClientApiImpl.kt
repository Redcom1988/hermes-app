package dev.redcom1988.hermes.data.remote.api.impl

import android.util.Log
import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.network.PUT
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.ClientApi
import dev.redcom1988.hermes.data.remote.model.ClientDto
import dev.redcom1988.hermes.data.remote.model.requests.AttendanceApiRequestDto
import dev.redcom1988.hermes.data.remote.model.requests.ClientApiRequestDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ClientApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com",
) : ClientApi {

    override suspend fun getClientData(): Response {
        val request = GET("$baseUrl/clients")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun pushClientChanges(request: ClientApiRequestDto): Response {
        val json = Json.encodeToString(ClientApiRequestDto.serializer(), request)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        Log.d("ASD", "pushClientChanges: $json")
        Log.d("ASD", "RequestBody: $requestBody")
        val requestObj = POST("$baseUrl/clients/sync", body = requestBody)
        return networkHelper.client.newCall(requestObj).await()
    }

}