package dev.redcom1988.hermes.domain.access

import kotlinx.coroutines.flow.Flow

interface AccessRepository {
    fun getAccessFlow(): Flow<List<Access>>
    suspend fun syncAccessesFromServer()
    suspend fun clearLocalAccesses()
}