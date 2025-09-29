package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.ClientDto
import dev.redcom1988.hermes.data.remote.model.requests.ClientApiRequestDto
import okhttp3.Response

interface ClientApi {
    suspend fun getClientData(): Response
    suspend fun pushClientChanges(request: ClientApiRequestDto): Response
}
