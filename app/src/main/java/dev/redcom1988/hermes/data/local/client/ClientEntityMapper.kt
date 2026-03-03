package dev.redcom1988.hermes.data.local.client

import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.domain.client.Client

fun ClientEntity.toDomain() = Client(
    id = clientId,
    name = fullName,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)

fun Client.toEntity(isSynced: Boolean = true) = ClientEntity(
    clientId = id,
    fullName = name,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    isSynced = isSynced,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)