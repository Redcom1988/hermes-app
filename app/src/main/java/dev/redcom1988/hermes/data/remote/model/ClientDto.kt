package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.client.Client
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDto (
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun ClientDto.toDomain(): Client {
    return Client(
        id = this.id,
        fullName = this.fullName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        address = this.address,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt,
    )
}
