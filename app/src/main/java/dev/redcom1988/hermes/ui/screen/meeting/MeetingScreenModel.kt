package dev.redcom1988.hermes.ui.screen.meeting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.account_data.model.User
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingRepository
import dev.redcom1988.hermes.domain.client.ClientRepository
import dev.redcom1988.hermes.domain.account_data.UserRepository
import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef
import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

data class MeetingUiState(
    val meetings: List<Meeting> = emptyList(),
    val filteredMeetings: List<Meeting> = emptyList(),
    val availableClients: List<Client> = emptyList(),
    val availableUsers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingMeeting: Meeting? = null,
)

class MeetingScreenModel : ScreenModel {
    private val meetingRepository: MeetingRepository by injectLazy()
    private val clientRepository: ClientRepository by injectLazy()
    private val userRepository: UserRepository by injectLazy()

    private val _state = MutableStateFlow(MeetingUiState())
    val state: StateFlow<MeetingUiState> = _state

    init {
        observeData()
    }

    private fun observeData() {
        combine(
            meetingRepository.getMeetingsFlow(),
            clientRepository.getVisibleClients(),
            userRepository.getVisibleUsers()
        ) { meetings, clients, users ->
            _state.value = _state.value.copy(
                meetings = meetings.filter { !it.isDeleted },
                availableClients = clients,
                availableUsers = users,
                isLoading = false,
                errorMessage = null
            )
            applyFilters()
        }.launchIn(screenModelScope)
    }

    private fun applyFilters() {
        val currentState = _state.value
        var filteredMeetings = currentState.meetings

        // Filter by search query (meeting title)
        if (currentState.searchQuery.isNotBlank()) {
            filteredMeetings = filteredMeetings.filter { meeting ->
                meeting.title.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        // Sort by start time (most recent first)
        filteredMeetings = filteredMeetings.sortedByDescending { it.startTime }

        _state.value = _state.value.copy(filteredMeetings = filteredMeetings)
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(
            showCreateDialog = true,
            editingMeeting = null
        )
    }

    fun showEditDialog(meeting: Meeting) {
        _state.value = _state.value.copy(
            showEditDialog = true,
            editingMeeting = meeting
        )
    }

    fun hideDialogs() {
        _state.value = _state.value.copy(
            showCreateDialog = false,
            showEditDialog = false,
            editingMeeting = null
        )
    }

    fun createMeeting(
        title: String,
        note: String?,
        startTime: String,
        endTime: String,
        selectedClientIds: List<Int>,
        selectedUserIds: List<Int>
    ) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val meetingId = meetingRepository.addMeeting(
                    title = title,
                    note = note,
                    startTime = startTime,
                    endTime = endTime
                )

                // Create CrossRef entries for clients
                selectedClientIds.forEach { clientId ->
                    meetingRepository.linkClient(meetingId, clientId)
                }

                // Create CrossRef entries for users
                selectedUserIds.forEach { userId ->
                    meetingRepository.linkUser(meetingId, userId)
                }

                hideDialogs()
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create meeting: ${e.message}"
                )
            }
        }
    }

    fun updateMeeting(meeting: Meeting) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                meetingRepository.update(meeting)
                hideDialogs()
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to update meeting: ${e.message}"
                )
            }
        }
    }

    fun deleteMeeting(meetingId: Int) {
        screenModelScope.launch {
            try {
                meetingRepository.deleteMeetingById(meetingId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to delete meeting: ${e.message}")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
