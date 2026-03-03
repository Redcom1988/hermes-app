package dev.redcom1988.hermes.ui.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.task.Task
import dev.redcom1988.hermes.domain.task.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class EditTaskUiState(
    val task: Task? = null,
    val taskName: String = "",
    val taskDescription: String = "",
    val selectedDate: LocalDate = LocalDate.now().plusDays(7),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isTaskUpdated: Boolean = false
)

class EditTaskScreenModel(
    private val taskId: Int
) : ScreenModel {
    private val taskRepository: TaskRepository by injectLazy()

    private val _state = MutableStateFlow(EditTaskUiState())
    val state: StateFlow<EditTaskUiState> = _state

    init {
        loadTask()
    }

    private fun loadTask() {
        taskRepository.getTaskById(taskId)
            .onEach { task ->
                if (task != null) {
                    val deadline = try {
                        LocalDate.parse(task.deadline, DateTimeFormatter.ISO_LOCAL_DATE)
                    } catch (e: DateTimeParseException) {
                        LocalDate.now().plusDays(7)
                    }

                    _state.value = _state.value.copy(
                        task = task,
                        taskName = task.name,
                        taskDescription = task.description ?: "",
                        selectedDate = deadline,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Task not found"
                    )
                }
            }
            .launchIn(screenModelScope)
    }

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

    fun saveTask() {
        val currentState = _state.value
        val task = currentState.task

        if (task == null) {
            _state.value = _state.value.copy(errorMessage = "Task not found")
            return
        }

        if (currentState.taskName.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Task name cannot be empty")
            return
        }

        screenModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)

            try {
                val updatedTask = task.copy(
                    name = currentState.taskName,
                    description = currentState.taskDescription.ifBlank { null },
                    deadline = currentState.selectedDate.toString()
                )

                taskRepository.update(updatedTask)

                _state.value = _state.value.copy(
                    isSaving = false,
                    isTaskUpdated = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = "Failed to update task: ${e.message}"
                )
            }
        }
    }
}
