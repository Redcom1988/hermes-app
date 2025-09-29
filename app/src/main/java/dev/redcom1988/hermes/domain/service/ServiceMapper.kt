package dev.redcom1988.hermes.domain.service

import dev.redcom1988.hermes.data.remote.model.ServiceDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeDataDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeFieldDto

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

fun ServiceType.toDto(): ServiceTypeDto {
    return ServiceTypeDto(
        id = id,
        name = name,
        description = description,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

fun ServiceTypeField.toDto(): ServiceTypeFieldDto {
    return ServiceTypeFieldDto(
        id = id,
        serviceTypeId = serviceTypeId,
        fieldName = fieldName,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

fun ServiceTypeDataCrossRef.toDto(): ServiceTypeDataDto {
    return ServiceTypeDataDto(
        fieldId = fieldId,
        serviceId = serviceId,
        value = value,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}