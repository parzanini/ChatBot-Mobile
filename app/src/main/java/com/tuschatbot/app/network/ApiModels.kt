package com.tuschatbot.app.network

// Request to send to the API with user's question
data class AskRequest(
    val query: String // User's question text
)

// Information about where the answer came from
data class Source(
    val title: String, // Title of the source document
    val source_name: String, // Name of the source (e.g., handbook name)
    val url: String?, // Link to the source (can be null)
    val score: Double // How relevant the source is (0-1)
)

// Debug information about the API response
data class Debug(
    val total_time_ms: Long // Total time the API took to respond in milliseconds
)

// Response received from the API with answer and sources
data class AskResponse(
    val answer: String, // The chatbot's answer to the question
    val sources: List<Source>, // List of sources used to generate the answer
    val debug: Debug? = null // Optional debug info (time, etc.)
)

// Request body for user registration
data class RegisterRequest(
    val email: String,
    val name: String,
    val surname: String,
    val password: String
)

data class UserDto(
    val email: String,
    val name: String,
    val surname: String,
    val role: String
)

// Response from registration endpoint
data class RegisterResponse(
    val success: Boolean,
    val message: String? = null,
    val detail: String? = null,
    val user: UserDto? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null,
    val detail: String? = null,
    val user: UserDto? = null
)

data class LogoutResponse(
    val success: Boolean,
    val message: String? = null,
    val detail: String? = null
)

