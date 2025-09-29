package dev.redcom1988.hermes.ui.screen.service

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.service.Service
import dev.redcom1988.hermes.domain.service.ServiceRepository
import dev.redcom1988.hermes.domain.service.ServiceType
import dev.redcom1988.hermes.domain.service.ServiceTypeDataCrossRef
import dev.redcom1988.hermes.domain.service.ServiceTypeField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ServiceUiState(
    val services: List<Service> = emptyList(),
    val serviceTypes: List<ServiceType> = emptyList(),
    val serviceTypeFields: List<ServiceTypeField> = emptyList(),
    val serviceTypeData: List<ServiceTypeDataCrossRef> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ServiceScreenModel : ScreenModel {
    val serviceRepository: ServiceRepository by injectLazy()

    private val _state = MutableStateFlow(ServiceUiState())
    val state: StateFlow<ServiceUiState> = _state

    init {
        observeServices()
    }

    fun observeServices() {
        combine(
            serviceRepository.getServicesFlow(),
            serviceRepository.getServiceTypesFlow(),
            serviceRepository.getServiceTypeFieldsFlow(),
            serviceRepository.getServiceTypeDataFlow()
        ) { services, serviceTypes, serviceTypeFields, serviceTypeData ->
            ServiceUiState(
                services = services,
                serviceTypes = serviceTypes,
                serviceTypeFields = serviceTypeFields,
                serviceTypeData = serviceTypeData,
                isLoading = false,
                error = null
            )
        }
            .onEach { _state.value = it }
            .launchIn(screenModelScope)
    }
}