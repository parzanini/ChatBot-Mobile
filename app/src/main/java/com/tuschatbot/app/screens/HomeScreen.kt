package com.tuschatbot.app.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.tuschatbot.app.components.AskBar
import com.tuschatbot.app.network.AskRequest
import com.tuschatbot.app.network.RetrofitClient
import kotlinx.coroutines.launch

data class Message(
    val text: String,
    val isUser: Boolean,
    val sources: List<Pair<String, String?>> = emptyList()
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var textInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val coroutineScope = rememberCoroutineScope()

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
                                            text = "Source: $source${if (url != null) " - $url" else ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
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

                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.apiService.ask(AskRequest(query = query))
                            val sources = response.sources.map { it.source_name to it.url }
                            messages = messages + Message(
                                text = response.answer,
                                isUser = false,
                                sources = sources
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            messages = messages + Message(
                                text = "Error: ${e.message}",
                                isUser = false
                            )
                        }
                    }
                }
            )
        }
    }
}

