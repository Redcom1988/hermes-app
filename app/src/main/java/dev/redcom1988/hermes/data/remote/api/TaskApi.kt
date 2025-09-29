package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.requests.TaskApiRequestDto
import okhttp3.Response

interface TaskApi {
    suspend fun getTaskData(): Response
    suspend fun pushTaskChanges(request: TaskApiRequestDto): Response
}