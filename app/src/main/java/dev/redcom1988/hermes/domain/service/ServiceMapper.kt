package dev.redcom1988.hermes.domain.service

import dev.redcom1988.hermes.data.remote.model.ServiceDto

fun Service.toDto(): ServiceDto {
    return ServiceDto(
        id = id,
        clientId = clientId,
        serviceTypeId = serviceTypeId,
        status = status,
        price = servicePrice,
        startTime = startTime,
        expireTime = expireTime,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}