package dev.redcom1988.hermes.data.local.access

import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.remote.api.AccessApi
import dev.redcom1988.hermes.data.remote.model.AccessDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.access.Access
import dev.redcom1988.hermes.domain.access.AccessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccessRepositoryImpl(
    private val accessDao: AccessDao,
    private val api: AccessApi
) : AccessRepository {

    override fun getAccessFlow(): Flow<List<Access>> {
        return accessDao.getVisibleAccessesFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncAccessesFromServer() {
        val response = api.getAccesses()
        if (response.isSuccessful) {
            val accesses = response.parseAs<List<AccessDto>>().map { it.toDomain().toEntity() }
            accessDao.insertAccesses(accesses)
        }
    }

    override suspend fun clearLocalAccesses() {
        accessDao.deleteAllAccesses()
    }

}