package dev.redcom1988.hermes.data.local.service

import dev.redcom1988.hermes.data.local.service.entity.ServiceEntity
import dev.redcom1988.hermes.domain.service.Service

fun ServiceEntity.toDomain() = Service(
    id = serviceId,
    clientId = clientId,
    serviceTypeId = serviceTypeId,
    status = status,
    servicePrice = servicePrice,
    startTime = startTime,
    expireTime = expireTime,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun Service.toEntity() = ServiceEntity(
    serviceId = id,
    clientId = clientId,
    serviceTypeId = serviceTypeId,
    status = status,
    servicePrice = servicePrice,
    startTime = startTime,
    expireTime = expireTime,
    isDeleted = isDeleted,
    updatedAt = updatedAt,
    createdAt = createdAt,
)