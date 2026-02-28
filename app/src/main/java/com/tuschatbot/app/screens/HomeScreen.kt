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
    // Keep track of sources we've already seen
    val seenSourceNames = mutableSetOf<String>()
    // Remove duplicate sources from the list
    return sources.filter { source ->
        if (source.source_name in seenSourceNames) {
            false // Skip this source (already seen)
        } else {
            seenSourceNames.add(source.source_name) // Remember this source
            true // Keep this source
        }
    }
}

// Data class to store a chat message with text, sources, and response time
data class Message(
    val text: String, // Message content
    val isUser: Boolean, // True if user sent it, false if bot sent it
    val sources: List<Pair<String, String?>> = emptyList(), // Source names and links
    val totalTimeMs: Long? = null // How long the bot took to respond
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Variable to store what user is typing
    var textInput by remember { mutableStateOf("") }
    // List of all messages in the chat (starts with welcome message)
    var messages by remember { mutableStateOf(listOf(
        Message(
            text = "Hi! I'm your TUS assistant. How can I help you today?",
            isUser = false
        )
    )) }
    // Flag to show/hide loading indicator
    var isLoading by remember { mutableStateOf(false) }
    // Scope for background tasks
    val coroutineScope = rememberCoroutineScope()
    // Access to Android context for opening links
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Scrollable list of messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Take up available space
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(messages) { message ->
                    // Display user message on the right side
                    if (message.isUser) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.CenterEnd // Right align
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary, // Blue color
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
                        // Display bot message on the left side
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant, // Light color
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Show sources if bot provided them
                            if (message.sources.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    // Display each source as a clickable link
                                    message.sources.forEach { (source, url) ->
                                        Text(
                                            text = "Source: $source${if (url != null) " - Click to open" else ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (url != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textDecoration = if (url != null) TextDecoration.Underline else null,
                                            modifier = Modifier
                                                .padding(vertical = 2.dp)
                                                .then(
                                                    // Make source clickable if it has a URL
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

                            // Show response time if available
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

                // Show loading message while waiting for response
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

            // Text input and send button at the bottom
            AskBar(
                text = textInput,
                onTextChange = { textInput = it }, // Update text as user types
                onSend = {
                    val query = textInput // Save the message
                    messages = messages + Message(text = query, isUser = true) // Add to chat
                    textInput = "" // Clear input field
                    isLoading = true // Show loading indicator

                    // Send message to API in background
                    coroutineScope.launch {
                        try {
                            // Call the chatbot API with the user's question
                            val response = RetrofitClient.apiService.ask(AskRequest(query = query))
                            val uniqueSources = filterDuplicateSources(response.sources) // Remove duplicates
                            val sources = uniqueSources.map { it.source_name to it.url } // Extract source info
                            // Add bot response to chat
                            messages = messages + Message(
                                text = response.answer,
                                isUser = false,
                                sources = sources,
                                totalTimeMs = response.debug?.total_time_ms // Include response time
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Show error message if something goes wrong
                            messages = messages + Message(
                                text = "Error: ${e.message}",
                                isUser = false
                            )
                        } finally {
                            isLoading = false // Hide loading indicator
                        }
                    }
                }
            )
        }
    }
}

