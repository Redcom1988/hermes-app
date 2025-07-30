package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.util.extension.await
import okhttp3.Response

class ServiceApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseURL: String = "https://api.example.com/"
) : ServiceApi {

    override suspend fun getServices() : Response {
        val request = GET("$baseURL/services")
        return networkHelper.client.newCall(request).await()
    }

}