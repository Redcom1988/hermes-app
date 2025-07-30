package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.TaskDto
import okhttp3.Response

interface TaskApi {
    suspend fun getTasks(): Response
    suspend fun createTask(task: TaskDto): Response
    suspend fun updateTask(id: Int, task: TaskDto): Response
}