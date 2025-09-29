package dev.redcom1988.hermes.data.remote.api.impl

import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.AuthApi
import dev.redcom1988.hermes.data.remote.model.requests.LoginRequestDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class AuthApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://mock-api.achmad.dev/hermes"
) : AuthApi {

    override suspend fun login(request: LoginRequestDto): Response {
        val jsonBody = Json.Default.encodeToString(LoginRequestDto.serializer(), request)
            .toRequestBody("application/json".toMediaType())
        val request = POST("$baseUrl/login", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun logout(): Response {
        val request = POST("$baseUrl/logout")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun refreshToken(): Response {
        val request = POST("$baseUrl/refresh")
        return networkHelper.client.newCall(request).await()
    }

}