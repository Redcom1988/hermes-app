package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.ClientDataDto
import dev.redcom1988.hermes.data.remote.model.ClientDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.ClientData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientApiResponseDto(
    val clients: List<ClientDto>?,
    @SerialName("client_data")
    val clientData: List<ClientDataDto>?
)

fun ClientApiResponseDto.toDomainClients(): List<Client> {
    return clients?.map { it.toDomain() } ?: emptyList()
}

fun ClientApiResponseDto.toDomainClientData(): List<ClientData> {
    return clientData?.map { it.toDomain() } ?: emptyList()
}