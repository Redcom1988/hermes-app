package dev.redcom1988.hermes.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkQuery
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dev.redcom1988.hermes.core.util.extension.HttpException
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.core.util.extension.isRunning
import dev.redcom1988.hermes.core.util.extension.workManager
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.auth.SyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

class SyncDataJob(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker (context, workerParams) {

    private val syncRepository: SyncRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()

    override suspend fun doWork(): Result {
        return try {
            val lastSyncTime = userPreference.lastSyncTime().get()
            val forceClear = inputData.getBoolean(KEY_FORCE_CLEAR, false)

            syncRepository.performSync(lastSyncTime, forceClear)
            Result.success()

        } catch (e: HttpException) {
            if (e.code == 404) {
                e.printStackTrace()
                Result.failure(workDataOf("error" to "Data not found on server"))
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "SyncData"
        private const val KEY_FORCE_CLEAR = "forceClear"

        // Shared StateFlow instance to prevent multiple observers
        @Volatile
        private var _sharedSyncStateFlow: StateFlow<Boolean>? = null

        fun syncState(
            context: Context,
            scope: CoroutineScope
        ): StateFlow<WorkInfo.State?> {
            val workQuery = WorkQuery.Builder
                .fromTags(listOf(WORK_NAME))
                .build()

            return context.workManager
                .getWorkInfosFlow(workQuery)
                .mapNotNull { it.firstOrNull()?.state }
                .stateIn(
                    scope = scope,
                    started = SharingStarted.Lazily,
                    initialValue = null
                )
        }

        fun isSyncing(context: Context, scope: CoroutineScope): StateFlow<Boolean> {
            // Clean up any potentially stale work entries first
            cleanupStaleWork(context)

            // Return existing shared instance if available
            _sharedSyncStateFlow?.let { return it }

            // Create new shared instance
            val workQuery = WorkQuery.Builder.fromTags(listOf(WORK_NAME)).build()
            val newStateFlow = context.workManager
                .getWorkInfosFlow(workQuery)
                .map { infos ->
                    val result = infos.any { workInfo ->
                        when (workInfo.state) {
                            WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> true
                            else -> false
                        }
                    }
                    Log.d("SyncState", "WorkInfos: ${infos.size}, States: ${infos.map { "${it.id}-${it.state}" }}, Result: $result")
                    result
                }
                .stateIn(
                    scope,
                    SharingStarted.WhileSubscribed(5000),
                    false
                )

            _sharedSyncStateFlow = newStateFlow
            return newStateFlow
        }

        // Clean up any stale work entries
        private fun cleanupStaleWork(context: Context) {
            try {
                val workManager = context.workManager
                // Cancel any stuck work items and clear completed ones
                workManager.cancelUniqueWork(WORK_NAME)
                workManager.pruneWork()
            } catch (e: Exception) {
                Log.e("SyncDataJob", "Failed to cleanup stale work", e)
            }
        }

        // Function to reset the shared state (useful for testing or when needed)
        fun resetSyncState() {
            _sharedSyncStateFlow = null
        }

        fun start(
            context: Context,
            forceClear: Boolean = false
        ): Boolean {
            val workManager = context.workManager
            if (workManager.isRunning(WORK_NAME)) {
                return false
            }

            val request = OneTimeWorkRequestBuilder<SyncDataJob>()
                .addTag(WORK_NAME)
                .setInputData(workDataOf(KEY_FORCE_CLEAR to forceClear))
                .build()
            workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
            return true
        }

        fun stop(
            context: Context
        ) {
            val workManager = context.workManager
            val workQuery = WorkQuery.Builder
                .fromTags(listOf(WORK_NAME))
                .addStates(listOf(WorkInfo.State.RUNNING))
                .build()
            workManager
                .getWorkInfos(workQuery).get()
                .forEach { workManager.cancelWorkById(it.id) }
        }
    }

}