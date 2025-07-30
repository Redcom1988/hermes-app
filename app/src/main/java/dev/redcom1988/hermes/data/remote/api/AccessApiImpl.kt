package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.util.extension.await
import okhttp3.Response

class AccessApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com",
) : AccessApi {

    override suspend fun getAccesses(): Response {
        val request = GET("$baseUrl/accesses")
        return networkHelper.client.newCall(request).await()
    }

}