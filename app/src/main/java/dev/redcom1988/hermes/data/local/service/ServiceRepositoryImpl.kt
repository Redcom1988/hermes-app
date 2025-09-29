package dev.redcom1988.hermes.data.local.service

import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.remote.api.ServiceApi
import dev.redcom1988.hermes.data.remote.model.ServiceDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.service.Service
import dev.redcom1988.hermes.domain.service.ServiceRepository
import dev.redcom1988.hermes.domain.service.ServiceType
import dev.redcom1988.hermes.domain.service.ServiceTypeDataCrossRef
import dev.redcom1988.hermes.domain.service.ServiceTypeField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao,
    private val api: ServiceApi
) : ServiceRepository {

    override fun getServicesFlow(): Flow<List<Service>> {
        return serviceDao.getVisibleServicesFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getServiceTypesFlow(): Flow<List<ServiceType>> {
        return serviceDao.getVisibleServiceTypesFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getServiceTypeFieldsFlow(): Flow<List<ServiceTypeField>> {
        return serviceDao.getVisibleServiceTypeFieldsFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getServiceTypeDataFlow(): Flow<List<ServiceTypeDataCrossRef>> {
        return serviceDao.getVisibleServiceTypeDataFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncServicesFromServer() {
        val response = api.getServiceData()
        if (response.isSuccessful) {
            val services = response.parseAs<List<ServiceDto>>().map { it.toDomain().toEntity() }
            serviceDao.insertServices(services)
        }
    }

    override suspend fun clearLocalServices() {
        serviceDao.deleteAllServices()
    }

}