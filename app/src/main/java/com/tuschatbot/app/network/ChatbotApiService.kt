package com.tuschatbot.app.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Interface that defines how to communicate with the chatbot API
interface ChatbotApiService {
    // Send a question to the API and get an answer back
    @POST("api/ask") // POST request to the /api/ask endpoint
    suspend fun ask(@Body request: AskRequest): AskResponse // Takes a request, returns a response

    @POST("api/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/logout/")
    suspend fun logout(@Header("Authorization") authorization: String): Response<LogoutResponse>
}

