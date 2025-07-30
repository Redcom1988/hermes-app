package dev.redcom1988.hermes.data.remote.model

import dev.redcom1988.hermes.domain.service.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    val id: Int,
    val clientId: Int,
    val serviceTypeId: Int,
    val status: String,
    val price: Int,
    val startTime: String,
    val expireTime: String,
    val isDeleted: Boolean,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

fun ServiceDto.toDomain() = Service(
    id = id,
    clientId = clientId,
    serviceTypeId = serviceTypeId,
    status = status,
    servicePrice = price,
    startTime = startTime,
    expireTime = expireTime,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)