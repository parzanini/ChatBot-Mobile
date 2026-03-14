package com.tuschatbot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.compose.TUSChatbotTheme
import com.tuschatbot.app.components.ChatTopBar
import com.tuschatbot.app.network.RetrofitClient
import com.tuschatbot.app.screens.HomeScreen
import com.tuschatbot.app.screens.HelpScreen
import com.tuschatbot.app.screens.LoginScreen
import com.tuschatbot.app.screens.RegisterScreen
import kotlinx.coroutines.launch

private enum class AppScreen {
    Login,
    Register,
    Home,
    Help
}

private enum class AuthMode {
    LoggedIn,
    Guest
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Show app behind system bars
        setContent {
            // Apply the app theme colors
            TUSChatbotTheme(darkTheme = false, dynamicColor = false) {
                val currentScreen = remember { mutableStateOf(AppScreen.Login) }
                val authMode = remember { mutableStateOf<AuthMode?>(null) }
                val authToken = remember { mutableStateOf<String?>(null) }
                val coroutineScope = rememberCoroutineScope()

                val exitToLogin: () -> Unit = {
                    coroutineScope.launch {
                        val token = authToken.value
                        if (!token.isNullOrBlank()) {
                            try {
                                RetrofitClient.apiService.logout("Bearer $token")
                            } catch (_: Exception) {
                                // Clear local session even if logout request fails.
                            }
                        }

                        authToken.value = null
                        authMode.value = null
                        currentScreen.value = AppScreen.Login
                    }
                }

                // Basic layout structure with top bar and content area
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ChatTopBar(
                            onHelpClick = { currentScreen.value = AppScreen.Help },
                            onBackClick = {
                                currentScreen.value = when (currentScreen.value) {
                                    AppScreen.Help -> AppScreen.Home
                                    AppScreen.Register -> AppScreen.Login
                                    else -> AppScreen.Home
                                }
                            },
                            showBackButton =
                                currentScreen.value == AppScreen.Help || currentScreen.value == AppScreen.Register,
                            showHelpButton = authMode.value != null && currentScreen.value == AppScreen.Home
                        )
                    }
                ) { innerPadding ->
                    when (currentScreen.value) {
                        AppScreen.Login -> {
                            LoginScreen(
                                modifier = Modifier.padding(innerPadding),
                                onLoginSuccess = { token ->
                                    authToken.value = token
                                    authMode.value = AuthMode.LoggedIn
                                    currentScreen.value = AppScreen.Home
                                },
                                onContinueAsGuest = {
                                    authToken.value = null
                                    authMode.value = AuthMode.Guest
                                    currentScreen.value = AppScreen.Home
                                },
                                onOpenRegister = {
                                    currentScreen.value = AppScreen.Register
                                }
                            )
                        }

                        AppScreen.Register -> {
                            RegisterScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackToLogin = { currentScreen.value = AppScreen.Login },
                                onRegisterSuccess = { token ->
                                    authToken.value = token
                                    authMode.value = AuthMode.LoggedIn
                                    currentScreen.value = AppScreen.Home
                                }
                            )
                        }

                        AppScreen.Help -> {
                            HelpScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBack = { currentScreen.value = AppScreen.Home },
                                onExitApp = exitToLogin
                            )
                        }

                        AppScreen.Home -> {
                            HomeScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}
