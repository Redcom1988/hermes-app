package dev.redcom1988.hermes.domain.service

import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getServicesFlow(): Flow<List<Service>>
    fun getServiceTypesFlow(): Flow<List<ServiceType>>
    fun getServiceTypeFieldsFlow(): Flow<List<ServiceTypeField>>
    fun getServiceTypeDataFlow(): Flow<List<ServiceTypeDataCrossRef>>
    suspend fun syncServicesFromServer()
    suspend fun clearLocalServices()
}