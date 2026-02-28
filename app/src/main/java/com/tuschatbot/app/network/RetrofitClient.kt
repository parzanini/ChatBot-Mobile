package com.tuschatbot.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton object that sets up and provides the API client
object RetrofitClient {
    // The server address where the API is hosted
    private const val BASE_URL = "https://chatbotproject-obag.onrender.com/"

    // Log all API requests and responses for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Show request/response bodies
    }

    // Configure the HTTP client with timeouts and logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Add logging to requests
        .connectTimeout(30, TimeUnit.SECONDS) // Wait max 30 seconds to connect
        .readTimeout(30, TimeUnit.SECONDS) // Wait max 30 seconds for response
        .writeTimeout(30, TimeUnit.SECONDS) // Wait max 30 seconds to send data
        .build()

    // Set up Retrofit to convert JSON responses to Kotlin objects
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // API server address
        .client(okHttpClient) // Use the configured HTTP client
        .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to objects
        .build()

    // Create the API service that the app can use to make requests
    val apiService: ChatbotApiService = retrofit.create(ChatbotApiService::class.java)
}

