package dev.redcom1988.hermes.domain.division

import kotlinx.coroutines.flow.Flow

interface DivisionRepository {
    fun getDivisionsFlow(): Flow<List<Division>>
    fun getDivisionsWithAccessFlow(): Flow<List<DivisionWithAccess>>
    suspend fun syncDivisionsFromServer()
    suspend fun clearLocalDivisions()
}