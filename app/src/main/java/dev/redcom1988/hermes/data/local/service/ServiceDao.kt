package dev.redcom1988.hermes.data.local.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.redcom1988.hermes.data.local.service.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<ServiceEntity>

    @Query("SELECT * FROM services WHERE isDeleted = 0")
    fun getVisibleServicesFlow(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(employees: List<ServiceEntity>)

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()


}