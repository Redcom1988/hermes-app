package dev.redcom1988.hermes.domain.client

import dev.redcom1988.hermes.data.remote.model.ClientDataDto
import dev.redcom1988.hermes.data.remote.model.ClientDto

fun Client.toDto(): ClientDto {
    return ClientDto(
        id = this.id,
        fullName = this.fullName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        address = this.address,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}

fun ClientData.toDto(): ClientDataDto {
    return ClientDataDto(
        id = this.id,
        clientId = this.clientId,
        accountType = this.accountType,
        accountCredentials = this.accountCredentials,
        accountPassword = this.accountPassword,
        isDeleted = this.isDeleted,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt
    )
}