package dev.redcom1988.hermes.data.local.division

import dev.redcom1988.hermes.core.util.extension.parseAs
import dev.redcom1988.hermes.data.local.division.entity.DivisionWithAccesses
import dev.redcom1988.hermes.data.local.division.entity.toDomain
import dev.redcom1988.hermes.data.remote.api.DivisionApi
import dev.redcom1988.hermes.data.remote.model.DivisionDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.division.Division
import dev.redcom1988.hermes.domain.division.DivisionRepository
import dev.redcom1988.hermes.domain.division.DivisionWithAccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class DivisionRepositoryImpl(
    private val divisionDao: DivisionDao,
    private val api: DivisionApi
) : DivisionRepository {

    override fun getDivisionsFlow(): Flow<List<Division>> {
        return divisionDao.getVisibleDivisionsFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getDivisionsWithAccessFlow(): Flow<List<DivisionWithAccess>> {
        return divisionDao.getVisibleDivisionsWithAccessesFlow()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncDivisionsFromServer() {
        val response = api.getDivisions()
        if (response.isSuccessful) {
            val divisions = response.parseAs<List<DivisionDto>>().map { it.toDomain().toEntity() }
            divisionDao.insertDivisions(divisions)
        }
    }

    override suspend fun clearLocalDivisions() {
        divisionDao.deleteAllDivisions()
    }

}