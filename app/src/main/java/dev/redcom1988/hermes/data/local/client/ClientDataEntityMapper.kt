package dev.redcom1988.hermes.data.local.client

import dev.redcom1988.hermes.data.local.client.entity.ClientDataEntity
import dev.redcom1988.hermes.domain.client.ClientData

fun ClientDataEntity.toDomain() = ClientData(
    id = dataId,
    clientId = clientId,
    accountType = accountType,
    accountCredentials = accountCredentials,
    accountPassword = accountPassword,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun ClientData.toEntity(isSynced: Boolean = true) = ClientDataEntity(
    dataId = id,
    clientId = clientId,
    accountType = accountType,
    accountCredentials = accountCredentials,
    accountPassword = accountPassword,
    isSynced = isSynced,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)