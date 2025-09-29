package dev.redcom1988.hermes.data.local.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.redcom1988.hermes.core.util.extension.toLocalDateTime
import dev.redcom1988.hermes.data.local.service.entity.ServiceEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeDataCrossRefEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeFieldEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<ServiceEntity>

    @Query("SELECT * FROM services WHERE isSynced = 0")
    suspend fun getPendingSyncServices(): List<ServiceEntity>

    @Query("SELECT * FROM service_type_data WHERE isSynced = 0")
    suspend fun getPendingSyncServiceTypeData(): List<ServiceTypeDataCrossRefEntity>

    @Query("SELECT * FROM services WHERE serviceId = :serviceId")
    suspend fun getServiceById(serviceId: Int): ServiceEntity?

    @Query("SELECT * FROM service_types WHERE serviceTypeId = :serviceTypeId")
    suspend fun getServiceTypeById(serviceTypeId: Int): ServiceTypeEntity?

    @Query("SELECT * FROM service_type_fields WHERE fieldId = :serviceTypeFieldId")
    suspend fun getServiceTypeFieldById(serviceTypeFieldId: Int): ServiceTypeFieldEntity?

    @Query("""UPDATE service_type_data 
        SET isDeleted = 0, isSynced = 0, updatedAt = :updatedAt 
        WHERE serviceId = :serviceId AND fieldId = :fieldId
        """)
    suspend fun reactivateLink(serviceId: Int, fieldId: Int, updatedAt: String)

    @Query("SELECT * FROM services WHERE clientId = :clientId")
    suspend fun getServicesByClientId(clientId: Int): List<ServiceEntity>

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Update
    suspend fun updateServiceType(serviceType: ServiceTypeEntity)

    @Update
    suspend fun updateServiceTypeField(serviceTypeField: ServiceTypeFieldEntity)

    @Update
    suspend fun updateServiceTypeData(serviceTypeDataCrossRefEntity: ServiceTypeDataCrossRefEntity)

    @Query("SELECT * FROM services WHERE isDeleted = 0")
    fun getVisibleServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM service_types WHERE isDeleted = 0")
    fun getVisibleServiceTypesFlow(): Flow<List<ServiceTypeEntity>>

    @Query("SELECT * FROM service_type_fields WHERE isDeleted = 0")
    fun getVisibleServiceTypeFieldsFlow(): Flow<List<ServiceTypeFieldEntity>>

    @Query("SELECT * FROM service_type_data WHERE isDeleted = 0")
    fun getVisibleServiceTypeDataFlow(): Flow<List<ServiceTypeDataCrossRefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceType(serviceType: ServiceTypeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceTypeField(serviceTypeField: ServiceTypeFieldEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceTypeData(data: ServiceTypeDataCrossRefEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(employees: List<ServiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceTypes(serviceTypes: List<ServiceTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceTypeFields(serviceTypeFields: List<ServiceTypeFieldEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceTypeData(data: List<ServiceTypeDataCrossRefEntity>)

    @Transaction
    suspend fun upsertService(service: ServiceEntity) {
        val existing = getServiceById(service.serviceId)
        if (existing != null) {
            updateService(service.copy(isSynced = false))
        } else {
            insertService(service)
        }
    }

    @Transaction
    suspend fun upsertRemoteService(service: ServiceEntity) {
        val existing = getServiceById(service.serviceId)
        if (existing != null) {
            updateService(service)
        } else {
            insertService(service)
        }
    }

    @Transaction
    suspend fun upsertServices(services: List<ServiceEntity>) {
        services.forEach { service ->
            upsertRemoteService(service)
        }
    }

    @Transaction
    suspend fun upsertServiceType(serviceType: ServiceTypeEntity) {
        val existing = getServiceTypeById(serviceType.serviceTypeId)
        if (existing != null) {
            updateServiceType(serviceType)
        } else {
            insertServiceType(serviceType)
        }
    }

    @Transaction
    suspend fun upsertServiceTypes(types: List<ServiceTypeEntity>) {
        types.forEach { type ->
            upsertServiceType(type)
        }
    }

    @Transaction
    suspend fun upsertServiceTypeField(serviceTypeField: ServiceTypeFieldEntity) {
        val existing = getServiceTypeFieldById(serviceTypeField.fieldId)
        if (existing != null) {
            updateServiceTypeField(serviceTypeField)
        } else {
            insertServiceTypeField(serviceTypeField)
        }
    }

    @Transaction
    suspend fun upsertServiceTypeFields(fields: List<ServiceTypeFieldEntity>) {
        fields.forEach { field ->
            upsertServiceTypeField(field)
        }
    }

    @Transaction
    suspend fun upsertServiceTypeData(data: ServiceTypeDataCrossRefEntity) {
        val existing = getServiceTypeFieldById(data.fieldId)
        if (existing != null) {
            updateServiceTypeData(data.copy(isSynced = false))
        } else {
            insertServiceTypeData(data)
        }
    }

    @Transaction
    suspend fun upsertRemoteServiceTypeData(data: ServiceTypeDataCrossRefEntity) {
        val existing = getServiceTypeFieldById(data.fieldId)
        if (existing != null) {
            updateServiceTypeData(data)
        } else {
            insertServiceTypeData(data)
        }
    }

    @Transaction
    suspend fun upsertServiceTypeData(remotes: List<ServiceTypeDataCrossRefEntity>) {
        remotes.forEach { remote ->
            upsertRemoteServiceTypeData(remote)
        }
    }

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()

    @Query("DELETE FROM service_types")
    suspend fun deleteAllServiceTypes()

    @Query("DELETE FROM service_type_fields")
    suspend fun deleteAllServiceTypeFields()

    @Query("DELETE FROM service_type_data")
    suspend fun deleteAllServiceTypeData()


}