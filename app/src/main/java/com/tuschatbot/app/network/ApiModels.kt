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

data class AskResponse(
    val answer: String,
    val sources: List<Source>
)

