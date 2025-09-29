package dev.redcom1988.hermes.data.remote.api.impl

import android.util.Log
import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.BulkSyncApi
import dev.redcom1988.hermes.data.remote.model.requests.BulkSyncApiRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

@Serializable
data class BulkSyncRequest(
    @SerialName("last_sync_time")
    val lastSyncTime: String
)

class BulkSyncApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://mock-api.achmad.dev/hermes"
) : BulkSyncApi {

    override suspend fun getLatestData(lastSyncTime: String): Response {
        val requestObj = GET("$baseUrl/sync?since=$lastSyncTime")
        Log.d("API", "RequestObj: $requestObj")
        return networkHelper.client.newCall(requestObj).await()
    }

    override suspend fun pushLocalChanges(request: BulkSyncApiRequest): Response {
        val json = Json.encodeToString(BulkSyncApiRequest.serializer(), request)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val requestObj = POST("$baseUrl/sync/push", body = requestBody)
        return networkHelper.client.newCall(requestObj).await()
    }
}