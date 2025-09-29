package dev.redcom1988.hermes.data.local.workhour_plan

import android.util.Log
import dev.redcom1988.hermes.core.util.extension.formattedNow
import dev.redcom1988.hermes.data.local.workhour_plan.entity.WorkhourPlanEntity
import dev.redcom1988.hermes.data.remote.api.WorkhourPlanApi
import dev.redcom1988.hermes.data.remote.model.requests.WorkhourPlanApiRequestDto
import dev.redcom1988.hermes.data.remote.model.responses.WorkhourPlanApiResponseDto
import dev.redcom1988.hermes.data.remote.model.responses.toDomainPlans
import dev.redcom1988.hermes.domain.common.WorkLocation
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlan
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlanRepository
import dev.redcom1988.hermes.domain.workhour_plan.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class WorkhourPlanRepositoryImpl(
    private val workhourPlanDao: WorkhourPlanDao,
    private val api: WorkhourPlanApi
) : WorkhourPlanRepository {

    suspend fun getMinTempPlanId(): Int? {
        val minTempId = workhourPlanDao.getMinTempPlanId() ?: 0
        return minTempId - 1
    }

    override suspend fun addPlan(
        employeeId: Int,
        planDate: String,
        plannedStartTime: String,
        plannedEndTime: String,
        workLocation: WorkLocation
    ): Int {
        val minTempId = getMinTempPlanId() ?: -1
        val entity = WorkhourPlanEntity(
            planId = minTempId,
            employeeId = employeeId,
            planDate = planDate,
            plannedStartTime = plannedStartTime,
            plannedEndTime = plannedEndTime,
            workLocation = workLocation,
            isSynced = false,
            isDeleted = false,
            createdAt = formattedNow(),
            updatedAt = formattedNow()
        )
        workhourPlanDao.upsertPlan(entity)
        return minTempId
    }

    override fun getVisiblePlans(): Flow<List<WorkhourPlan>> {
        return workhourPlanDao.getVisibleWorkhourPlans()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun update(plan: WorkhourPlan) {
        val updated = plan.copy(updatedAt = formattedNow())
        workhourPlanDao.upsertPlan(updated.toEntity(isSynced = false))
    }

    override suspend fun softDeletePlan(planId: Int) {
        workhourPlanDao.softDeleteWorkhourPlanById(planId, formattedNow())
    }

    override suspend fun insertPlans(plans: List<WorkhourPlan>) {
        workhourPlanDao.insertWorkhourPlans(plans.map { it.toEntity() })
    }

    override suspend fun getPendingSyncPlans(): List<WorkhourPlan> {
        return workhourPlanDao.getPendingSyncWorkhourPlans().map { it.toDomain() }
    }

    override suspend fun deleteAllPlans() {
        workhourPlanDao.deleteAllWorkhourPlans()
    }

//    private suspend fun fetchDataFromRemote(): WorkhourPlanApiResponseDto {
//        val response = api.getWorkhourPlans()
//        if (!response.isSuccessful) {
//            throw Exception("Failed to fetch workhour plans: ${response.code}")
//        }
//
//        val bodyString = response.body.string()
//        return Json.decodeFromString<WorkhourPlanApiResponseDto>(bodyString)
//    }
//
//    suspend fun upsertDataFromRemote(response: WorkhourPlanApiResponseDto) {
//        val remotePlans = response.toDomainPlans()
//
//        remotePlans.forEach { plan ->
//            val entity = plan.toEntity()
//            workhourPlanDao.upsertRemotePlanIfClean(entity)
//        }
//    }
//
//    suspend fun pushChangesToRemote(pendingPlans: List<WorkhourPlan>) {
//        if (pendingPlans.isEmpty()) return
//
//        val requestDto = WorkhourPlanApiRequestDto(
//            workhourPlans = pendingPlans.map { it.toDto() }
//        )
//        api.pushWorkhourPlanChanges(requestDto)
//    }
//
//    override suspend fun syncPlans() {
//        Log.d("ASD", "syncPlans: start")
//        val pendingPlans = getPendingSyncPlans()
//        pushChangesToRemote(pendingPlans)
//        val response = fetchDataFromRemote()
//        upsertDataFromRemote(response)
//    }

}