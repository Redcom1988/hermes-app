package dev.redcom1988.hermes.data.remote.api.impl

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.AccountApi
import okhttp3.Response

class AccountApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://mock-api.achmad.dev/hermes"
) : AccountApi {

    override suspend fun getAccountData(): Response {
        val request = GET("$baseUrl/account")
        return networkHelper.client.newCall(request).await()
    }

}