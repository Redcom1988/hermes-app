package dev.redcom1988.hermes.ui.screen.meeting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.domain.meeting.MeetingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateMeetingScreenModel : ScreenModel, KoinComponent {

    private val repository: MeetingRepository by inject()

    private val _state = MutableStateFlow(CreateMeetingState())
    val state: StateFlow<CreateMeetingState> = _state.asStateFlow()

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

    fun createMeeting() {
        val currentState = _state.value

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
                repository.addMeeting(
                    title = currentState.title.trim(),
                    note = currentState.note.trim().takeIf { it.isNotBlank() },
                    startTime = startDateTime,
                    endTime = endDateTime
                )
                _state.update { it.copy(isLoading = false, isMeetingCreated = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to create meeting: ${e.message}"
                    )
                }
            }
        }
    }
}

data class CreateMeetingState(
    val title: String = "",
    val note: String = "",
    val startDate: String = LocalDate.now().toString(),
    val startTime: String = "09:00",
    val endDate: String = LocalDate.now().toString(),
    val endTime: String = "10:00",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isMeetingCreated: Boolean = false
)
