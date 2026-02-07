package com.tuschatbot.app.network

import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApiService {
    @POST("api/ask")
    suspend fun ask(@Body request: AskRequest): AskResponse
}

