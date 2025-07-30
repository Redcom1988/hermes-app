package dev.redcom1988.hermes.domain.service

import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getServicesFlow(): Flow<List<Service>>
    suspend fun syncServicesFromServer()
    suspend fun clearLocalServices()
}