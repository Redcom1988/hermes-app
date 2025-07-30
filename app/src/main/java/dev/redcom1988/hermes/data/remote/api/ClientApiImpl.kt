package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.network.PUT
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.model.ClientDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ClientApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com",
) : ClientApi {

    override suspend fun getClients(): Response {
        val request = GET("$baseUrl/clients")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun createClient(client: ClientDto): Response {
        val jsonBody = Json.encodeToString(client)
            .toRequestBody("application/json".toMediaType())
        val request = POST("$baseUrl/clients", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun updateClient(id: Int, client: ClientDto): Response {
        val jsonBody = Json.encodeToString(client)
            .toRequestBody("application/json".toMediaType())
        val request = PUT("$baseUrl/clients/$id", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

}