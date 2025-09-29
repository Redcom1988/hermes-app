package dev.redcom1988.hermes.domain.client

import dev.redcom1988.hermes.core.util.extension.formattedNow

data class ClientData(
    val id: Int,
    val clientId: Int,
    val accountType: String,
    val accountCredentials: String,
    val accountPassword: String,
    val isDeleted: Boolean = false,
    val updatedAt: String = formattedNow(),
    val createdAt: String = formattedNow(),
)