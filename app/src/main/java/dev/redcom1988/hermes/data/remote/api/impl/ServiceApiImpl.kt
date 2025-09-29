package dev.redcom1988.hermes.data.remote.api.impl

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.data.remote.api.ServiceApi
import okhttp3.Response

class ServiceApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseURL: String = "https://api.example.com/"
) : ServiceApi {

    override suspend fun getServiceData(): Response {
        val request = GET("$baseURL/services")
        return networkHelper.client.newCall(request).await()
    }

}