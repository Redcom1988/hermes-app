package dev.redcom1988.hermes.data.remote.api

import okhttp3.Response

interface UserApi {
    suspend fun getUsers(): Response
}