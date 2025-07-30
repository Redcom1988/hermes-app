package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.ClientDto
import okhttp3.Response

interface ClientApi {
    suspend fun getClients(): Response
    suspend fun createClient(client: ClientDto): Response
    suspend fun updateClient(id: Int, client: ClientDto): Response
}
