package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.requests.MeetingApiRequestDto
import okhttp3.Response

interface MeetingApi {
    suspend fun getMeetingData(): Response
    suspend fun pushMeetingChanges(request: MeetingApiRequestDto): Response
}