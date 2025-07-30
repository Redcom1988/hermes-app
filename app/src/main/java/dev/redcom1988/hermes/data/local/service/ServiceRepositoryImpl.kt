package dev.redcom1988.hermes.data.local.service

import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.remote.api.ServiceApi
import dev.redcom1988.hermes.data.remote.model.ServiceDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.service.Service
import dev.redcom1988.hermes.domain.service.ServiceRepository
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

    override suspend fun syncServicesFromServer() {
        val response = api.getServices()
        if (response.isSuccessful) {
            val services = response.parseAs<List<ServiceDto>>().map { it.toDomain().toEntity() }
            serviceDao.insertServices(services)
        }
    }

    override suspend fun clearLocalServices() {
        serviceDao.deleteAllServices()
    }

}