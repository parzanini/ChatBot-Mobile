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
import com.tuschatbot.app.network.RegisterRequest
import com.tuschatbot.app.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackToLogin: () -> Unit,
    onRegisterSuccess: (String?) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
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
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Use your details to register.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Surname") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

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
                val cleanName = name.trim()
                val cleanSurname = surname.trim()

                when {
                    cleanEmail.isBlank() -> feedbackMessage = "Please enter your email."
                    !cleanEmail.contains("@") -> feedbackMessage = "Please enter a valid email address."
                    cleanName.isBlank() -> feedbackMessage = "Please enter your name."
                    cleanSurname.isBlank() -> feedbackMessage = "Please enter your surname."
                    password.isBlank() -> feedbackMessage = "Please enter your password."
                    password.length < 6 -> feedbackMessage = "Password must be at least 6 characters."
                    else -> {
                        coroutineScope.launch {
                            isLoading = true
                            feedbackMessage = null

                            try {
                                val registerResponse = RetrofitClient.apiService.register(
                                    RegisterRequest(
                                        email = cleanEmail,
                                        name = cleanName,
                                        surname = cleanSurname,
                                        password = password
                                    )
                                )

                                val registerBody = registerResponse.body()
                                if (registerResponse.isSuccessful && registerBody?.success == true) {
                                    val loginResponse = RetrofitClient.apiService.login(
                                        LoginRequest(email = cleanEmail, password = password)
                                    )
                                    val loginBody = loginResponse.body()

                                    if (loginResponse.isSuccessful && loginBody?.success == true && !loginBody.token.isNullOrBlank()) {
                                        onRegisterSuccess(loginBody.token)
                                    } else {
                                        feedbackMessage = "Account created, but automatic login failed. Please try logging in."
                                    }
                                } else {
                                    val rawError = registerResponse.errorBody()?.string()
                                    val backendMessage = registerBody?.message
                                        ?: registerBody?.detail
                                        ?: extractApiMessage(rawError)
                                    feedbackMessage = backendMessage ?: "Registration failed. Please check your details and try again."
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
            Text(if (isLoading) "Registering..." else "Register")
        }

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
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

