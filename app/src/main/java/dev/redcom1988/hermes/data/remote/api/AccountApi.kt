package dev.redcom1988.hermes.data.remote.api

import okhttp3.Response

interface AccountApi {
    suspend fun getAccountData(): Response
}