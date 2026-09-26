package com.example.medreminders.ui

import androidx.lifecycle.ViewModel
import com.example.medreminders.data.Task
import com.example.medreminders.data.sampleTasks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val tasks: List<Task> = sampleTasks,
    val toastMessage: String? = null,
)

class TaskViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    fun addTask(task: Task) {
        _uiState.update {
            it.copy(
                tasks = it.tasks + task,
                toastMessage = "Recordatorio guardado correctamente.",
            )
        }
    }

    fun deleteTask(id: String) {
        _uiState.update {
            it.copy(
                tasks = it.tasks.filter { t -> t.id != id },
                toastMessage = "Recordatorio eliminado.",
            )
        }
    }

    fun clearToast() {
        _uiState.update {
            it.copy(toastMessage = null)
        }
    }
}