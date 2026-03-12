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
import androidx.compose.ui.Modifier
import com.example.compose.TUSChatbotTheme
import com.tuschatbot.app.components.ChatTopBar
import com.tuschatbot.app.screens.HomeScreen
import com.tuschatbot.app.screens.HelpScreen
import com.tuschatbot.app.screens.LoginScreen
import com.tuschatbot.app.screens.RegisterScreen

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
                            onLogoutClick = {
                                authMode.value = null
                                currentScreen.value = AppScreen.Login
                            },
                            showBackButton =
                                currentScreen.value == AppScreen.Help || currentScreen.value == AppScreen.Register,
                            showHelpButton = authMode.value != null && currentScreen.value == AppScreen.Home,
                            showLogoutButton = authMode.value != null
                        )
                    }
                ) { innerPadding ->
                    when (currentScreen.value) {
                        AppScreen.Login -> {
                            LoginScreen(
                                modifier = Modifier.padding(innerPadding),
                                onLogin = {
                                    authMode.value = AuthMode.LoggedIn
                                    currentScreen.value = AppScreen.Home
                                },
                                onContinueAsGuest = {
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
                                onRegisterSuccess = {
                                    authMode.value = AuthMode.LoggedIn
                                    currentScreen.value = AppScreen.Home
                                }
                            )
                        }

                        AppScreen.Help -> {
                            HelpScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBack = { currentScreen.value = AppScreen.Home }
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
