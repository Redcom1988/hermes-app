package dev.redcom1988.hermes.ui.screen.meeting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingRepository
import dev.redcom1988.hermes.domain.client.Client
import dev.redcom1988.hermes.domain.client.ClientRepository
import dev.redcom1988.hermes.domain.account_data.model.User
import dev.redcom1988.hermes.domain.account_data.UserRepository
import dev.redcom1988.hermes.domain.meeting.MeetingClientCrossRef
import dev.redcom1988.hermes.domain.meeting.MeetingUserCrossRef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

data class MeetingDetailUiState(
    val meeting: Meeting? = null,
    val assignedClients: List<Client> = emptyList(),
    val assignedUsers: List<User> = emptyList(),
    val availableClients: List<Client> = emptyList(),
    val availableUsers: List<User> = emptyList(),
    val clientCrossRefs: List<MeetingClientCrossRef> = emptyList(),
    val userCrossRefs: List<MeetingUserCrossRef> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showEditMeetingDialog: Boolean = false,
    val showAddClientDialog: Boolean = false,
    val showAddUserDialog: Boolean = false
)

class MeetingDetailScreenModel(
    private val meetingId: Int
) : ScreenModel {
    private val meetingRepository: MeetingRepository by injectLazy()
    private val clientRepository: ClientRepository by injectLazy()
    private val userRepository: UserRepository by injectLazy()

    private val _state = MutableStateFlow(MeetingDetailUiState(isLoading = true))
    val state: StateFlow<MeetingDetailUiState> = _state

    init {
        observeMeeting()
    }

    private fun observeMeeting() {
        combine(
            meetingRepository.getMeetingsFlow(),
            meetingRepository.getClientLinkFlow(),
            meetingRepository.getUserLinkFlow(),
            clientRepository.getVisibleClients(),
            userRepository.getVisibleUsers()
        ) { meetings, clientCrossRefs, userCrossRefs, allClients, allUsers ->
            val meeting = meetings.firstOrNull { it.id == meetingId && !it.isDeleted }

            // Get active client cross-refs for this meeting
            val activeClientCrossRefs = clientCrossRefs.filter {
                it.meetingId == meetingId && !it.isDeleted
            }
            val assignedClientIds = activeClientCrossRefs.map { it.clientId }
            val assignedClients = allClients.filter { it.id in assignedClientIds }

            // Get active user cross-refs for this meeting
            val activeUserCrossRefs = userCrossRefs.filter {
                it.meetingId == meetingId && !it.isDeleted
            }
            val assignedUserIds = activeUserCrossRefs.map { it.userId }
            val assignedUsers = allUsers.filter { it.id in assignedUserIds }

            _state.value = _state.value.copy(
                meeting = meeting,
                assignedClients = assignedClients,
                assignedUsers = assignedUsers,
                availableClients = allClients,
                availableUsers = allUsers,
                clientCrossRefs = activeClientCrossRefs,
                userCrossRefs = activeUserCrossRefs,
                isLoading = false,
                errorMessage = null
            )
        }.launchIn(screenModelScope)
    }

    fun openEditMeetingDialog() {
        _state.value = _state.value.copy(showEditMeetingDialog = true)
    }

    fun hideEditMeetingDialog() {
        _state.value = _state.value.copy(showEditMeetingDialog = false)
    }

    fun openAddClientDialog() {
        _state.value = _state.value.copy(showAddClientDialog = true)
    }

    fun hideAddClientDialog() {
        _state.value = _state.value.copy(showAddClientDialog = false)
    }

    fun openAddUserDialog() {
        _state.value = _state.value.copy(showAddUserDialog = true)
    }

    fun hideAddUserDialog() {
        _state.value = _state.value.copy(showAddUserDialog = false)
    }

    fun updateMeeting(meeting: Meeting) {
        screenModelScope.launch {
            try {
                meetingRepository.update(meeting)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to update meeting: ${e.message}")
            }
        }
    }

    fun addClientToMeeting(clientId: Int) {
        screenModelScope.launch {
            try {
                meetingRepository.linkClient(meetingId, clientId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to add client: ${e.message}")
            }
        }
    }

    fun addUserToMeeting(userId: Int) {
        screenModelScope.launch {
            try {
                meetingRepository.linkUser(meetingId, userId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to add user: ${e.message}")
            }
        }
    }

    fun removeClientFromMeeting(client: Client) {
        screenModelScope.launch {
            try {
                meetingRepository.unlinkClient(meetingId, client.id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to remove client: ${e.message}")
            }
        }
    }

    fun removeUserFromMeeting(user: User) {
        screenModelScope.launch {
            try {
                meetingRepository.unlinkUser(meetingId, user.id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to remove user: ${e.message}")
            }
        }
    }
}
