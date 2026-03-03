package dev.redcom1988.hermes.ui.screen.meeting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.domain.meeting.Meeting
import dev.redcom1988.hermes.domain.meeting.MeetingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditMeetingScreenModel(private val meetingId: Int) : ScreenModel, KoinComponent {

    private val repository: MeetingRepository by inject()

    private val _state = MutableStateFlow(EditMeetingState())
    val state: StateFlow<EditMeetingState> = _state.asStateFlow()

    init {
        loadMeeting()
    }

    private fun loadMeeting() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getMeetingById(meetingId).collectLatest { meeting ->
                if (meeting != null) {
                    // Parse start and end time to separate date and time
                    val (startDate, startTime) = try {
                        val startDateTime = LocalDateTime.parse(meeting.startTime)
                        startDateTime.toLocalDate().toString() to startDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: Exception) {
                        "" to ""
                    }

                    val (endDate, endTime) = try {
                        val endDateTime = LocalDateTime.parse(meeting.endTime)
                        endDateTime.toLocalDate().toString() to endDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: Exception) {
                        "" to ""
                    }

                    _state.update {
                        it.copy(
                            meeting = meeting,
                            title = meeting.title,
                            note = meeting.note ?: "",
                            startDate = startDate,
                            startTime = startTime,
                            endDate = endDate,
                            endTime = endTime,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, meeting = null) }
                }
            }
        }
    }

    fun updateTitle(value: String) {
        _state.update { it.copy(title = value) }
    }

    fun updateNote(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun updateStartDate(value: String) {
        _state.update { it.copy(startDate = value) }
    }

    fun updateStartTime(value: String) {
        _state.update { it.copy(startTime = value) }
    }

    fun updateEndDate(value: String) {
        _state.update { it.copy(endDate = value) }
    }

    fun updateEndTime(value: String) {
        _state.update { it.copy(endTime = value) }
    }

    fun updateMeeting() {
        val currentState = _state.value
        val meeting = currentState.meeting ?: return

        if (currentState.title.isBlank()) {
            _state.update { it.copy(errorMessage = "Meeting title is required") }
            return
        }

        if (currentState.startDate.isBlank() || currentState.startTime.isBlank()) {
            _state.update { it.copy(errorMessage = "Start date and time are required") }
            return
        }

        if (currentState.endDate.isBlank() || currentState.endTime.isBlank()) {
            _state.update { it.copy(errorMessage = "End date and time are required") }
            return
        }

        // Format to ISO datetime format
        val startDateTime = try {
            "${currentState.startDate}T${currentState.startTime}:00"
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Invalid start date/time format") }
            return
        }

        val endDateTime = try {
            "${currentState.endDate}T${currentState.endTime}:00"
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Invalid end date/time format") }
            return
        }

        // Validate that end is after start
        try {
            val start = LocalDateTime.parse(startDateTime)
            val end = LocalDateTime.parse(endDateTime)
            if (end.isBefore(start) || end.isEqual(start)) {
                _state.update { it.copy(errorMessage = "End time must be after start time") }
                return
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Invalid date/time: ${e.message}") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val updatedMeeting = meeting.copy(
                    title = currentState.title.trim(),
                    note = currentState.note.trim().takeIf { it.isNotBlank() },
                    startTime = startDateTime,
                    endTime = endDateTime
                )
                repository.update(updatedMeeting)
                _state.update { it.copy(isLoading = false, isMeetingUpdated = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update meeting: ${e.message}"
                    )
                }
            }
        }
    }
}

data class EditMeetingState(
    val meeting: Meeting? = null,
    val title: String = "",
    val note: String = "",
    val startDate: String = "",
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isMeetingUpdated: Boolean = false
)
