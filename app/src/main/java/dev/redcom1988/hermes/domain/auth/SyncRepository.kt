package dev.redcom1988.hermes.domain.auth


interface SyncRepository {
    suspend fun performSync(lastSyncTime: String, forceClearDataOverride: Boolean = false)
    suspend fun clearLocalData()
}