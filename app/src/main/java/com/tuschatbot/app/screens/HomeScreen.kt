package com.tuschatbot.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tuschatbot.app.components.AskBar
import com.tuschatbot.app.network.AskRequest
import com.tuschatbot.app.network.RetrofitClient
import com.tuschatbot.app.network.Source
import kotlinx.coroutines.launch

fun filterDuplicateSources(sources: List<Source>): List<Source> {
    val seenSourceNames = mutableSetOf<String>()
    return sources.filter { source ->
        if (source.source_name in seenSourceNames) {
            false
        } else {
            seenSourceNames.add(source.source_name)
            true
        }
    }
}

data class Message(
    val text: String,
    val isUser: Boolean,
    val sources: List<Pair<String, String?>> = emptyList(),
    val totalTimeMs: Long? = null
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var textInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(messages) { message ->
                    if (message.isUser) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (message.sources.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    message.sources.forEach { (source, url) ->
                                        Text(
                                            text = "Source: $source${if (url != null) " - Click to open" else ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (url != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textDecoration = if (url != null) TextDecoration.Underline else null,
                                            modifier = Modifier
                                                .padding(vertical = 2.dp)
                                                .then(
                                                    if (url != null) {
                                                        Modifier.clickable {
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                            context.startActivity(intent)
                                                        }
                                                    } else {
                                                        Modifier
                                                    }
                                                )
                                        )
                                    }
                                }
                            }

                            if (message.totalTimeMs != null) {
                                Text(
                                    text = "Query time: ${message.totalTimeMs}ms",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Loading indicator
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Loading your answer...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            AskBar(
                text = textInput,
                onTextChange = { textInput = it },
                onSend = {
                    val query = textInput
                    messages = messages + Message(text = query, isUser = true)
                    textInput = ""
                    isLoading = true

                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.apiService.ask(AskRequest(query = query))
                            val uniqueSources = filterDuplicateSources(response.sources)
                            val sources = uniqueSources.map { it.source_name to it.url }
                            messages = messages + Message(
                                text = response.answer,
                                isUser = false,
                                sources = sources,
                                totalTimeMs = response.debug?.total_time_ms
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            messages = messages + Message(
                                text = "Error: ${e.message}",
                                isUser = false
                            )
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )
        }
    }
}

