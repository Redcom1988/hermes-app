package dev.redcom1988.hermes.data.remote.api

import dev.redcom1988.hermes.data.remote.model.MeetingDto
import okhttp3.Response

interface MeetingApi {
    suspend fun getMeetings(): Response
    suspend fun createMeeting(meeting: MeetingDto): Response
    suspend fun updateMeeting(id: Int, meeting: MeetingDto): Response
}