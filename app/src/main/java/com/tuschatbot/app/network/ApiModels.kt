package com.tuschatbot.app.network

data class AskRequest(
    val query: String
)

data class AskResponse(
    val response: String? = null
)

