package dev.redcom1988.hermes.data.remote.api

import okhttp3.Response

interface AuthApi {
    suspend fun login(username: String, password: String): Response
    suspend fun logout(): Response
    suspend fun refreshToken(): Response
}