package dev.redcom1988.hermes.domain.account_data

import dev.redcom1988.hermes.data.local.account_data.AccountData
import dev.redcom1988.hermes.data.local.account_data.AccountDataType
import dev.redcom1988.hermes.data.remote.model.responses.AccountApiResponseDto
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccountDataFlow(): Flow<AccountData>
//    suspend fun fetchAccountDataFromRemote(): AccountApiResponseDto
//    suspend fun syncAccountData()
    suspend fun clearLocalAccountData()
//    suspend fun upsertAccountData(data: Any, dataType: AccountDataType)
}