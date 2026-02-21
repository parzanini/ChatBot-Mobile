package com.tuschatbot.app.network

data class AskRequest(
    val query: String
)

data class Source(
    val title: String,
    val source_name: String,
    val url: String?,
    val score: Double
)

data class Debug(
    val total_time_ms: Long
)

data class AskResponse(
    val answer: String,
    val sources: List<Source>,
    val debug: Debug? = null
)

