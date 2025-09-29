package dev.redcom1988.hermes.data.remote.model.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientSyncResponseDto(
    @SerialName("client_id_map")
    val clientIdMap: Map<Int, Int> = emptyMap(),
    @SerialName("client_data_id_map")
    val clientDataIdMap: Map<Int, Int> = emptyMap(),
)