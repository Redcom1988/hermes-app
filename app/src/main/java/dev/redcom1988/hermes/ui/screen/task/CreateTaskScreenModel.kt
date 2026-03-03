package dev.redcom1988.hermes.ui.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.task.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CreateTaskUiState(
    val taskName: String = "",
    val taskDescription: String = "",
    val selectedDate: LocalDate = LocalDate.now().plusDays(7),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTaskCreated: Boolean = false
)

class CreateTaskScreenModel : ScreenModel {
    private val taskRepository: TaskRepository by injectLazy()

    private val _state = MutableStateFlow(CreateTaskUiState())
    val state: StateFlow<CreateTaskUiState> = _state

    fun updateTaskName(name: String) {
        _state.value = _state.value.copy(taskName = name)
    }

    fun updateTaskDescription(description: String) {
        _state.value = _state.value.copy(taskDescription = description)
    }

    fun updateSelectedDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun createTask() {
        val currentState = _state.value
        
        if (currentState.taskName.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Task name cannot be empty")
            return
        }

        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                taskRepository.addTask(
                    name = currentState.taskName,
                    description = currentState.taskDescription.ifBlank { null },
                    deadline = currentState.selectedDate.toString(),
                    parentTaskId = null
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    isTaskCreated = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create task: ${e.message}"
                )
            }
        }
    }
}
