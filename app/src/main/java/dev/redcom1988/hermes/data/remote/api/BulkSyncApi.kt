package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.requests.BulkSyncApiRequest
import okhttp3.Response

interface BulkSyncApi {
    suspend fun getLatestData(lastSyncTime: String): Response
    suspend fun pushLocalChanges(request: BulkSyncApiRequest): Response
}