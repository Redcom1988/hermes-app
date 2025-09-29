package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.client.ClientData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDataDto(
    val id: Int,
    @SerialName("client_id")
    val clientId: Int,
    @SerialName("account_type")
    val accountType: String,
    @SerialName("account_credentials")
    val accountCredentials: String,
    @SerialName("account_password")
    val accountPassword: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun ClientDataDto.toDomain(): ClientData {
    return ClientData(
        id = id,
        clientId = clientId,
        accountType = accountType,
        accountCredentials = accountCredentials,
        accountPassword = accountPassword,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}