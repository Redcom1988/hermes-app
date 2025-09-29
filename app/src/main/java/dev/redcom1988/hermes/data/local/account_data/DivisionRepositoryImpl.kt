package dev.redcom1988.hermes.data.local.account_data

import dev.redcom1988.hermes.data.local.account_data.dao.DivisionDao
import dev.redcom1988.hermes.domain.account_data.DivisionRepository

class DivisionRepositoryImpl(
    private val divisionDao: DivisionDao,
) : DivisionRepository {

    override suspend fun getDivisionWorkHoursByName(divisionName: String): Int? {
        return divisionDao.getDivisionByName(divisionName)?.requiredWorkHours
    }

    override suspend fun getDivisionById(divisionId: Int): String? {
        return divisionDao.getDivisionById(divisionId)?.divisionName
    }

}