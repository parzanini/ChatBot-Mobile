package com.tuschatbot.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.tuschatbot.app.network.LoginRequest
import com.tuschatbot.app.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onOpenRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val cleanEmail = email.trim()
                when {
                    cleanEmail.isBlank() -> {
                        feedbackMessage = "Please enter your email."
                    }

                    !cleanEmail.contains("@") -> {
                        feedbackMessage = "Please enter a valid email address."
                    }

                    password.isBlank() -> {
                        feedbackMessage = "Please enter your password."
                    }

                    else -> {
                        coroutineScope.launch {
                            isLoading = true
                            feedbackMessage = null

                            try {
                                val response = RetrofitClient.apiService.login(
                                    LoginRequest(email = cleanEmail, password = password)
                                )

                                val body = response.body()
                                if (response.isSuccessful && body?.success == true && !body.token.isNullOrBlank()) {
                                    onLoginSuccess(body.token)
                                } else {
                                    val rawError = response.errorBody()?.string()
                                    val backendMessage = body?.message
                                        ?: body?.detail
                                        ?: extractApiMessage(rawError)
                                    feedbackMessage = backendMessage ?: "Login failed. Please check your credentials and try again."
                                }
                            } catch (_: Exception) {
                                feedbackMessage = "We could not connect right now. Please try again in a moment."
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Signing in..." else "Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onContinueAsGuest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Guest")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onOpenRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        if (!feedbackMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feedbackMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun extractApiMessage(rawErrorBody: String?): String? {
    if (rawErrorBody.isNullOrBlank()) return null
    return try {
        val json = JSONObject(rawErrorBody)
        json.optString("message").ifBlank {
            json.optString("detail").ifBlank { null }
        }
    } catch (_: Exception) {
        null
    }
}


