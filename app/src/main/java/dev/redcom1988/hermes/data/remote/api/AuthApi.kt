package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.requests.LoginRequestDto
import okhttp3.Response

interface AuthApi {
    suspend fun login(request: LoginRequestDto): Response
    suspend fun logout(): Response
    suspend fun refreshToken(): Response
}