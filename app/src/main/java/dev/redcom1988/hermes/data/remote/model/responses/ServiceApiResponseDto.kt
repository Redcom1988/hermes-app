package dev.redcom1988.hermes.data.remote.model.responses

import dev.redcom1988.hermes.data.remote.model.ServiceDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeDataDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeDto
import dev.redcom1988.hermes.data.remote.model.ServiceTypeFieldDto
import dev.redcom1988.hermes.data.remote.model.toDomain
import dev.redcom1988.hermes.domain.service.Service
import dev.redcom1988.hermes.domain.service.ServiceType
import dev.redcom1988.hermes.domain.service.ServiceTypeDataCrossRef
import dev.redcom1988.hermes.domain.service.ServiceTypeField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceApiResponseDto(
    val services: List<ServiceDto>?,
    @SerialName("service_types")
    val serviceTypes: List<ServiceTypeDto>?,
    @SerialName("service_type_fields")
    val serviceTypeFields: List<ServiceTypeFieldDto>?,
    @SerialName("service_type_data")
    val serviceTypeData: List<ServiceTypeDataDto>?,
)

fun ServiceApiResponseDto.toDomainServices(): List<Service> {
    return services?.map { it.toDomain() } ?: emptyList()
}

fun ServiceApiResponseDto.toDomainServiceTypes(): List<ServiceType> {
    return serviceTypes?.map { it.toDomain() } ?: emptyList()
}

fun ServiceApiResponseDto.toDomainServiceTypeFields(): List<ServiceTypeField> {
    return serviceTypeFields?.map { it.toDomain() } ?: emptyList()
}

fun ServiceApiResponseDto.toDomainServiceTypeData(): List<ServiceTypeDataCrossRef> {
    return serviceTypeData?.map { it.toDomain() } ?: emptyList()
}