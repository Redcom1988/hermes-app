package dev.redcom1988.hermes.data.remote.model.requests

import dev.redcom1988.hermes.data.remote.model.ClientDataDto
import dev.redcom1988.hermes.data.remote.model.ClientDto
import dev.redcom1988.hermes.data.remote.model.ServiceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientApiRequestDto(
    val clients: List<ClientDto>? = emptyList(),
    @SerialName("client_data")
    val clientData: List<ClientDataDto>? = emptyList(),
    val services: List<ServiceDto>? = emptyList()
)