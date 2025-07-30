package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.network.POST
import dev.redcom1988.hermes.core.util.extension.await
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class AuthApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com/"
) : AuthApi {

    override suspend fun login(email: String, password: String): Response {
        val loginRequest = loginRequestDto(email, password)
        val jsonBody = Json.Default.encodeToString(LoginRequestDto.serializer(), loginRequest)
            .toRequestBody("application/json".toMediaType())
        val request = POST("$baseUrl/api/login", body = jsonBody)
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun logout(): Response {
        val request = POST("$baseUrl/api/logout")
        return networkHelper.client.newCall(request).await()
    }

    override suspend fun refreshToken(): Response {
        val request = POST("$baseUrl/api/refresh")
        return networkHelper.client.newCall(request).await()
    }

}