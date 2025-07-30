package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.util.extension.await
import okhttp3.Response

class EmployeeApiImpl(
    private val networkHelper: NetworkHelper,
    private val baseUrl: String = "https://api.example.com/"
) : EmployeeApi {

    override suspend fun getEmployees(): Response {
        val request = GET("$baseUrl/employees")
        return networkHelper.client.newCall(request).await()
    }

}